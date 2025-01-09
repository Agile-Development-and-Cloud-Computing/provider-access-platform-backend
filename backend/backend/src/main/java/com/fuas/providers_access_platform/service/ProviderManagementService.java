package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.repository.RoleOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<Map<String,Object>> getAllOffersGrouped() {
        // Fetch all role offers from the database
        List<RoleOffer> roleOffers = roleOfferRepository.findAll();

        System.out.println("Rows"+roleOffers.toString());
        // Group by roleName, masterAgreementTypeName, and domainId
        Map<String, List<RoleOffer>> groupedOffers = roleOffers.stream().collect(Collectors.groupingBy(
                offer -> offer.getRoleName() + "|" + offer.getMasterAgreementTypeName() + "|"
        ));

        System.out.println("Rows"+ groupedOffers.toString());
        // Transform grouped data into List<Map<String, Object>>
        return groupedOffers.values().stream().map(offers -> {
            RoleOffer firstOffer = offers.get(0);

            // Create a map for the main response object
            Map<String, Object> response = new HashMap<>();
            response.put("roleName", firstOffer.getRoleName());
            response.put("experienceLevel", firstOffer.getExperienceLevel());
            response.put("technologiesCatalog", firstOffer.getTechnologiesCatalog());
            response.put("domainName", firstOffer.getDomainName());
            response.put("masterAgreementTypeId", firstOffer.getMasterAgreementTypeId());
            response.put("masterAgreementTypeName", firstOffer.getMasterAgreementTypeName());

            // Map providers as a list of maps
            List<Map<String, Object>> providers = offers.stream().map(offer -> {
                Map<String, Object> provider = new HashMap<>();
                provider.put("offerId", offer.getId());
                provider.put("providerName", offer.getProvider());
                provider.put("quotePrice", offer.getQuotePrice());
                provider.put("offerCycle", offer.getOfferCycle());
                return provider;
            }).collect(Collectors.toList());

            response.put("providers", providers);
            return response;
        }).collect(Collectors.toList());
    }

    public void updateOfferResponse(Long offerId, Boolean isAccepted) {
        // Fetch the record by offerId
        RoleOffer offer = roleOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer with ID " + offerId + " not found."));

        // Update the isAccepted field;

        // Save the updated offer back to the database
        roleOfferRepository.save(offer);
    }
}
