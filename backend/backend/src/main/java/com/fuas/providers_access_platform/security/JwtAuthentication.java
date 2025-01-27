package com.fuas.providers_access_platform.security;

import com.fuas.providers_access_platform.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthentication extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthentication.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {
        String token = getTokenFromRequest(request);

        if (token != null) {
            logger.info("JWT Token detected, processing authentication.");

            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String userType = jwtService.extractUserType(token);

                logger.info("Token is valid for user: {}, with userType: {}", username, userType);

                // Manually create an Authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                // Store userType in the request attributes (for further processing if needed)
                request.setAttribute("userType", userType);
                logger.debug("UserType set in request attribute: {}", userType);

                // Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication set in security context for user: {}", username);
            } else {
                logger.warn("Invalid or expired JWT token received.");
            }
        } else {
            logger.debug("No JWT token found in request.");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            logger.debug("Authorization header found, extracting token.");
            return bearerToken.substring(7);
        }

        logger.debug("No Bearer token found in Authorization header.");
        return null;
    }
}
