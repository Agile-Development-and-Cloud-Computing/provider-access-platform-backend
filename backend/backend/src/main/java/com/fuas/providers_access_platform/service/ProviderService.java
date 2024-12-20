package com.fuas.providers_access_platform.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean updateProviderName(String providerId, String newProviderName) {
        String updateQuery = "UPDATE providers SET provider_name = ? WHERE provider_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(updateQuery, newProviderName, providerId);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
