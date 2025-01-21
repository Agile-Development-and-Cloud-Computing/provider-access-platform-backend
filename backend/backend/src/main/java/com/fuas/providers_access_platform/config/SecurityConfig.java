package com.fuas.providers_access_platform.config;

import com.fuas.providers_access_platform.security.JwtAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthentication jwtAuthentication;

    // Read the authFlag from application.properties
    @Value("${authFlag}")
    private int authFlag;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for all requests (as it is unnecessary for stateless API)
        http.csrf(csrf -> csrf.disable()) // `csrf().disable()` still works here
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/service-request/published/{providerId}", "/api/provider/master-agreements","api/provider/bid","/api/employees/{providerId}").permitAll() // Whitelist these URLs
                        .anyRequest().authenticated()  // Require authentication for other requests
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Ensure stateless session for JWT
                );

        // Add the JWT authentication filter to the filter chain if authFlag is 1
        if (authFlag == 1) {
            http.addFilterBefore(jwtAuthentication, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
