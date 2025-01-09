package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.service.MasterAgreementService;
import com.fuas.providers_access_platform.service.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
public class ProviderManagementController {
    @Autowired
    private ProviderManagementService providerManagementService;

    @Autowired
    private MasterAgreementService masterAgreementService;

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
    public CommonResponse getMasterAgreementsWithRoleOffer() {
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
    public ResponseEntity<Map<String, String>> postMaOfferResponse(@RequestBody Map<String, Object> request) {
        Long offerId = Long.valueOf(request.get("offerId").toString());
        Boolean isAccepted = Boolean.valueOf(request.get("isAccepted").toString());

        providerManagementService.updateOfferResponse(offerId, isAccepted);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Offer Response updated successfully"
        ));
    }
}
