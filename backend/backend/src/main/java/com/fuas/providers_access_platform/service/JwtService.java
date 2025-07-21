package com.fuas.providers_access_platform.service;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // Secret Key - Can be dynamically generated or statically provided
    private Key secretKey;

    // Constructor to generate the key when JwtService is instantiated
    public JwtService() {
        try {
            logger.info("Initializing JwtService and generating secret key.");
            this.secretKey = generateSecretKey();
            logger.info("Secret key successfully generated.");
        } catch (Exception e) {
            logger.error("Error generating secret key: {}", e.getMessage(), e);
        }
    }

    // Method to generate a random secret key
    private Key generateSecretKey() throws Exception {
        logger.debug("Generating HMAC-SHA256 secret key.");
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // Generate JWT Token
    public String generateToken(String username, String userType) {
        logger.info("Generating token for user: {}, userType: {}", username, userType);
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("userType", userType)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Token successfully generated for user: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Error generating token", e);
        }
    }

    // Validate JWT Token
    public boolean validateToken(String token) {
        logger.info("Validating token.");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.warn("Invalid token signature: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage(), e);
        }
        return false;
    }

    // Extract Username from Token
    public String extractUsername(String token) {
        logger.info("Extracting username from token.");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            logger.debug("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting username", e);
        }
    }

    // Extract User Type from Token
    public String extractUserType(String token) {
        logger.info("Extracting user type from token.");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userType = (String) claims.get("userType");
            logger.debug("Extracted userType: {}", userType);
            return userType;
        } catch (Exception e) {
            logger.error("Error extracting user type from token: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting user type", e);
        }
    }
}
