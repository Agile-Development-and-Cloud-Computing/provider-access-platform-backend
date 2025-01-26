package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.model.RoleOffer;
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
@CrossOrigin
@RequestMapping("/api/provider")
public class ProviderManagementController {
    @Autowired
    private ProviderManagementService providerManagementService;

    @Autowired
    private MasterAgreementService masterAgreementService;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(ProviderManagementController.class);


    @PutMapping("/edit-credentials")
    public ResponseEntity<Map<String, Object>> editProviderCredentials(@RequestBody Map<String, String> inputPayload) {
        String providerId = inputPayload.get("providerId");
        String newProviderName = inputPayload.get("newProviderName");

        boolean success = providerManagementService.updateProviderName(providerId, newProviderName);

        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "Provider credentials updated successfully.");
        } else {
            response.put("success", false);
            response.put("message", "Failed to update provider credentials.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/configure-user")
    public ResponseEntity<CommonResponse> configureProviderUser(@RequestBody ProviderRequest request) {
        CommonResponse response = providerManagementService.configureUser(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/master-agreements")
    public CommonResponse getMasterAgreementsWithRoleOffer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                logger.info("Token is valid for user: {}", username);
            } else {
                logger.error("Invalid or expired token.");
                return new CommonResponse(false, "Unauthorized", null);
            }
        } else {
            return new CommonResponse(false, "Missing token", null);
        }

        List<Map<String, Object>> masterAgreements = masterAgreementService.getMasterAgreementsWithRoleOffer();
        return new CommonResponse(true, "Offers fetched successfully", masterAgreements);
    }

    @PostMapping("/create-offer")
    public CommonResponse createOffer(@RequestBody MasterAgreementRequest masterAgreementRequest) {
        // Call service to handle the logic
        CommonResponse response = masterAgreementService.createMasterAgreementOffer(masterAgreementRequest);
        return response;
    }


    @GetMapping("/role-offers")
    public ResponseEntity<CommonResponse<List<Map<String,Object>>>> getAllOffersGrouped() {
        // Fetch grouped offers from service
        List<Map<String,Object>> groupedOffers = providerManagementService.getAllOffersGrouped();

        // Build response
        CommonResponse<List<Map<String,Object>>> response = new CommonResponse<>();
        response.setSuccess(true);
        response.setMessage("Data retrieved successfully");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setData(groupedOffers);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/offer-response")
    public ResponseEntity<Map<String, String>> postMaOfferResponse(@RequestBody List<Map<String, Object>> requestList) {
        try {
            providerManagementService.updateOfferResponse(requestList);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Offer Response updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failure",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "failure",
                    "message", "An unexpected error occurred while updating offer responses"
            ));
        }
    }

    @PostMapping("/bid")
    public ResponseEntity<Map<String, Object>> createRoleOffer(@RequestBody RoleOffer request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean offer = masterAgreementService.updateOffer(request);

            if (!offer) {
                response.put("success", false);
                response.put("message", "The offer does not exist. Please contact the Admin.");
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("message", "Role Offer successfully created");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating Role Offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
