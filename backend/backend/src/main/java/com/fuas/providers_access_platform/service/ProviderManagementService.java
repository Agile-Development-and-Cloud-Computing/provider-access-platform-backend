package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.repository.RoleOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProviderManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleOfferRepository roleOfferRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProviderManagementService.class);


    public List<Map<String, Object>> getAllUsers() {
        String query = "SELECT username, password, user_type, id, email, provider_id, provider_name, cycle_status FROM user";
        try {
            logger.info("Fetching all users from database.");
            return jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            logger.error("Error fetching users from the database: {}", e.getMessage(), e);
            return null;
        }
    }


    public User updateProviderDetails(String providerId, String newProviderName, String newEmail, String newPassword, String newUsername) {
        String updateQuery = "UPDATE user SET provider_name = ?, email = ?, password = ?, username = ? WHERE provider_id = ?";

        try {
            // Update the provider details in the database
            int rowsAffected = jdbcTemplate.update(updateQuery, newProviderName, newEmail, newPassword, newUsername, providerId);

            if (rowsAffected > 0) {
                // Successfully updated, fetch the updated user
                String query = "SELECT * FROM user WHERE provider_id = ?";
                User updatedUser = jdbcTemplate.queryForObject(query, new Object[]{providerId}, new BeanPropertyRowMapper<>(User.class));

                // Log and return the updated user data
                logger.info("Successfully updated provider details for providerId: {}", providerId);
                return updatedUser;
            } else {
                // No rows affected, log the failure
                logger.warn("No rows affected. Update may not have been successful for providerId: {}", providerId);
                return null;
            }
        } catch (Exception e) {
            // Log the error and return null in case of exception
            logger.error("Error updating provider details for providerId: {}", providerId, e);
            return null;
        }
    }



    public CommonResponse<Object> configureUser(ProviderRequest request) {
        String checkUserCountQuery = "SELECT COUNT(*) FROM provider_users WHERE provider_id = ?";
        String insertUserQuery = "INSERT INTO provider_users (provider_id, username, email) VALUES (?, ?, ?)";

        logger.info("Checking the current user count for providerId: {}", request.getProviderId());
        // Check if the provider already has 2 users
        int userCount = jdbcTemplate.queryForObject(checkUserCountQuery, Integer.class, request.getProviderId());
        if (userCount >= 2) {
            logger.warn("Maximum of 2 users already configured for providerId: {}", request.getProviderId());
            return new CommonResponse<>(false, "Maximum of 2 users already configured for this provider.", null);
        }

        logger.info("Inserting a new user for providerId: {} with username: {}", request.getProviderId(), request.getUsername());
        // Insert the new user
        int rowsInserted = jdbcTemplate.update(insertUserQuery, request.getProviderId(), request.getUsername(), request.getEmail());

        if (rowsInserted > 0) {
            logger.info("User added successfully for providerId: {}", request.getProviderId());
            return new CommonResponse<>(true, "User added successfully for the provider.", null);
        } else {
            logger.warn("Failed to add user for providerId: {}", request.getProviderId());
            return new CommonResponse<>(false, "Failed to add user.", null);
        }
    }


    public List<Map<String, Object>> getAllOffersGrouped() {
        logger.info("Fetching all role offers from the database.");
        // Fetch all role offers from the database
        List<RoleOffer> roleOffers = roleOfferRepository.findAll();

        logger.debug("Fetched role offers: {}", roleOffers);

        // Grouping by experienceLevel, roleName, masterAgreementTypeName, and domainId
        Map<String, List<RoleOffer>> groupedOffers = roleOffers.stream().collect(Collectors.groupingBy(
                offer -> offer.getExperienceLevel() + "|" +
                        offer.getRoleName() + "|" +
                        offer.getMasterAgreementTypeName()
        ));

        logger.debug("Grouped offers by experience level, role name, and master agreement type: {}", groupedOffers);

        // Transform grouped data into List<Map<String, Object>>
        return groupedOffers.values().stream().map(offers -> {
            RoleOffer firstOffer = offers.get(0);

            // Create a map for the main response object
            Map<String, Object> response = new HashMap<>();
            response.put("experienceLevel", firstOffer.getExperienceLevel());
            response.put("masterAgreementTypeId", firstOffer.getMasterAgreementTypeId());
            response.put("domainName", firstOffer.getDomainName());
            response.put("roleName", firstOffer.getRoleName());
            response.put("technologiesCatalog", firstOffer.getTechnologiesCatalog());
            response.put("masterAgreementTypeName", firstOffer.getMasterAgreementTypeName());

            // Grouping providers by quotePrice to separate data by unique price levels
            Map<Double, List<RoleOffer>> groupedByPrice = offers.stream()
                    .collect(Collectors.groupingBy(RoleOffer::getQuotePrice));

            List<Map<String, Object>> providers = groupedByPrice.values().stream().flatMap(group ->
                    group.stream().map(offer -> {
                        Map<String, Object> provider = new HashMap<>();
                        provider.put("offerId", offer.getId());
                        provider.put("providerName", offer.getProvider());
                        provider.put("quotePrice", offer.getQuotePrice());
                        provider.put("bidPrice", offer.getBidPrice());
                        provider.put("roleId", offer.getRoleId());
                        provider.put("providerId", offer.getProviderId());
                        return provider;
                    })
            ).collect(Collectors.toList());

            response.put("providers", providers);
            return response;
        }).collect(Collectors.toList());
    }

    public void updateOfferResponse(List<Map<String, Object>> requestList) {
        logger.info("Updating offer responses for a list of {} offers.", requestList.size());

        for (Map<String, Object> request : requestList) {
            Long offerId = Long.valueOf(request.get("offerId").toString());
            String offerCycle = String.valueOf(request.get("cycle").toString());
            Boolean isAccepted = Boolean.valueOf(request.get("isAccepted").toString());

            logger.info("Updating offerId: {} with cycle: {} and isAccepted: {}", offerId, offerCycle, isAccepted);

            // Fetch the record by offerId
            RoleOffer offer = roleOfferRepository.findById(offerId)
                    .orElseThrow(() -> {
                        logger.error("Offer with ID {} not found.", offerId);
                        return new IllegalArgumentException("Offer with ID " + offerId + " not found.");
                    });

            // Update the isAccepted field
            offer.setisAccepted(isAccepted);
            offer.setOfferCycle(offerCycle);

            // Save the updated offer back to the database
            roleOfferRepository.save(offer);
            logger.info("OfferId: {} updated successfully.", offerId);
        }
    }
}
