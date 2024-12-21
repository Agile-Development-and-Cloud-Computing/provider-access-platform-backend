package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ProviderManagementService {

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

    public CommonResponse<Object> configureUser(ProviderRequest request) {
        String checkUserCountQuery = "SELECT COUNT(*) FROM provider_users WHERE provider_id = ?";
        String insertUserQuery = "INSERT INTO provider_users (provider_id, username, email) VALUES (?, ?, ?)";

        // Check if the provider already has 2 users
        int userCount = jdbcTemplate.queryForObject(checkUserCountQuery, Integer.class, request.getProviderId());
        if (userCount >= 2) {
            return new CommonResponse<>(false, "Maximum of 2 users already configured for this provider.", null);
        }

        // Insert the new user
        int rowsInserted = jdbcTemplate.update(insertUserQuery, request.getProviderId(), request.getUsername(), request.getEmail());

        if (rowsInserted > 0) {
            return new CommonResponse<>(true, "User added successfully for the provider.", null);
        } else {
            return new CommonResponse<>(false, "Failed to add user.", null);
        }
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
