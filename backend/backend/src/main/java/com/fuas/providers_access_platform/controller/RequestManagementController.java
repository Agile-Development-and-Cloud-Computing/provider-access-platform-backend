package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.AgreementOfferResponse;
import com.fuas.providers_access_platform.service.RequestManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RequestManagementController {


    @Autowired
    private RequestManagementService requestManagementService;

    @GetMapping("/agreement-offers")
    public List<AgreementOfferResponse> getAgreementOffers() {
        return requestManagementService.getAgreementOffers();
    }

    @PostMapping("/post-ma-offer-response")
    public ResponseEntity<Map<String, String>> postMaOfferResponse(@RequestBody Map<String, Object> request) {
        Long offerId = Long.valueOf(request.get("offerId").toString());
        Boolean isAccepted = Boolean.valueOf(request.get("isAccepted").toString());

        requestManagementService.updateOfferResponse(offerId, isAccepted);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Response Posted successfully"
        ));
    }
}
