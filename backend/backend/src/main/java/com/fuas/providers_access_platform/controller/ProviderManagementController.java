package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementResponse;
import com.fuas.providers_access_platform.dto.ProviderRequest;
import com.fuas.providers_access_platform.service.MasterAgreementService;
import com.fuas.providers_access_platform.service.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("master-agreements")
    public CommonResponse getMasterAgreementsWithRoleOffer() {
        List<MasterAgreementResponse> masterAgreements = masterAgreementService.getMasterAgreementsWithRoleOffer();
        return new CommonResponse(true, "Offers fetched successfully", masterAgreements);
    }

}
