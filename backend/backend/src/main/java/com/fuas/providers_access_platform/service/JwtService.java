package com.fuas.providers_access_platform.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Secret Key - Can be dynamically generated or statically provided
    private Key secretKey;

    // Constructor to generate the key when JwtService is instantiated
    public JwtService() {
        try {
            this.secretKey = generateSecretKey(); // Generate a new secret key
        } catch (Exception e) {
            e.printStackTrace();  // Log error in a real application
        }
    }

    // Method to generate a random secret key
    private Key generateSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256); // 256-bit key for HMACSHA256
        return keyGen.generateKey();
    }

    // Generate JWT Token
    public String generateToken(String username, String userType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType)  // Add custom claim
                .setIssuedAt(new Date())  // Set issued date
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Sign with the secret key
                .compact();
    }

    // Validate JWT Token
    public boolean validateToken(String token) {
        try {
            // Parse the token and verify using the secret key
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;  // Token is valid
        } catch (Exception e) {
            return false;  // Token is invalid or expired
        }
    }

    // Extract Username from Token
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // Extracts the username from token
    }

    // Extract User Type from Token
    public String extractUserType(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("userType");  // Extract user type from custom claim
    }
}
