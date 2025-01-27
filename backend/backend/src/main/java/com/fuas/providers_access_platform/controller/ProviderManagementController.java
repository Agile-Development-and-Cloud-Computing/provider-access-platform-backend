package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.service.JwtService;
import com.fuas.providers_access_platform.service.MasterAgreementService;
import com.fuas.providers_access_platform.service.ProviderManagementService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("/api/provider")
public class ProviderManagementController {
    @Autowired
    private ProviderManagementService providerManagementService;

    @Autowired
    private MasterAgreementService masterAgreementService;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(ProviderManagementController.class);


    @GetMapping("/get-user")
    public CommonResponse getAllUsers() {
        logger.info("Received request to get all users.");
        List<Map<String, Object>> users = providerManagementService.getAllUsers();
        logger.info("Successfully retrieved {} users.", users.size());
        return new CommonResponse(true, "Users retrieved successfully", users);
    }

    @PutMapping("/edit-credentials")
    public ResponseEntity<CommonResponse> editProviderCredentials(@RequestBody Map<String, String> inputPayload) {
        logger.info("Received request to update provider credentials: {}", inputPayload);

        // Extracting input values
        String providerId = inputPayload.get("providerId");
        String newProviderName = inputPayload.get("newProviderName");
        String newEmail = inputPayload.get("newEmail");
        String newPassword = inputPayload.get("newPassword");
        String newUsername = inputPayload.get("newUsername");

        CommonResponse response;

        // Input validation
        if (providerId == null || providerId.isEmpty() ||
                newProviderName == null || newProviderName.isEmpty() ||
                newEmail == null || newEmail.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                newUsername == null || newUsername.isEmpty()) {

            logger.warn("Invalid input data. Some fields are missing.");
            response = new CommonResponse(false, "All fields (providerId, newProviderName, newEmail, newPassword, newUsername) are required.", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Call the service method to update provider details and fetch updated user
        User updatedUser = providerManagementService.updateProviderDetails(providerId, newProviderName, newEmail, newPassword, newUsername);

        if (updatedUser != null) {
            response = new CommonResponse(true, "Provider credentials updated successfully.", updatedUser); // Return the updated User data
            logger.info("Provider credentials updated successfully for providerId: {}", providerId);
        } else {
            logger.warn("Failed to update provider credentials for providerId: {}", providerId);
            response = new CommonResponse(false, "Failed to update provider credentials. Please check the provided data.", null);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/configure-user")
    public ResponseEntity<CommonResponse> configureProviderUser(@RequestBody ProviderRequest request) {
        logger.info("Received request to configure provider user: {}", request);

        CommonResponse response = providerManagementService.configureUser(request);

        if (response.isSuccess()) {
            logger.info("User configured successfully: {}", request.getUsername());
        } else {
            logger.warn("User configuration failed: {}", request.getUsername());
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/master-agreements")
    public CommonResponse getMasterAgreementsWithRoleOffer(HttpServletRequest request) {
        logger.info("Fetching master agreements with role offer");

        List<Map<String, Object>> masterAgreements = masterAgreementService.getMasterAgreementsWithRoleOffer();
        logger.info("Successfully retrieved {} master agreements", masterAgreements.size());

        return new CommonResponse(true, "Offers fetched successfully", masterAgreements);
    }

    @PostMapping("/create-offer")
    public CommonResponse createOffer(@RequestBody MasterAgreementRequest masterAgreementRequest) {
        logger.info("Received request to create a new master agreement offer: {}", masterAgreementRequest);

        CommonResponse response = masterAgreementService.createMasterAgreementOffer(masterAgreementRequest);

        if (response.isSuccess()) {
            logger.info("Master agreement offer created successfully");
        } else {
            logger.warn("Failed to create master agreement offer");
        }

        return response;
    }


    @GetMapping("/role-offers")
    public ResponseEntity<CommonResponse<List<Map<String, Object>>>> getAllOffersGrouped() {
        logger.info("Fetching all grouped role offers");

        List<Map<String, Object>> groupedOffers = providerManagementService.getAllOffersGrouped();

        CommonResponse<List<Map<String, Object>>> response = new CommonResponse<>();
        response.setSuccess(true);
        response.setMessage("Data retrieved successfully");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setData(groupedOffers);

        logger.info("Retrieved {} grouped role offers", groupedOffers.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/offer-response")
    public ResponseEntity<Map<String, String>> postMaOfferResponse(@RequestBody List<Map<String, Object>> requestList) {
        logger.info("Received request to update offer response for {} items", requestList.size());

        try {
            providerManagementService.updateOfferResponse(requestList);
            logger.info("Offer responses updated successfully");
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Offer Response updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update offer response: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failure",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating offer responses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "failure",
                    "message", "An unexpected error occurred while updating offer responses"
            ));
        }
    }

    @PostMapping("/bid")
    public ResponseEntity<Map<String, Object>> createRoleOffer(@RequestBody RoleOffer request) {
        logger.info("Received request to create a role offer: {}", request);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean offer = masterAgreementService.updateOffer(request);

            if (!offer) {
                logger.warn("Role offer creation failed, offer does not exist: {}", request);
                response.put("success", false);
                response.put("message", "The offer does not exist. Please contact the Admin.");
                return ResponseEntity.ok(response);
            }

            logger.info("Role Offer successfully created for: {}", request);
            response.put("success", true);
            response.put("message", "Role Offer successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating Role Offer", e);
            response.put("success", false);
            response.put("message", "Error creating Role Offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
