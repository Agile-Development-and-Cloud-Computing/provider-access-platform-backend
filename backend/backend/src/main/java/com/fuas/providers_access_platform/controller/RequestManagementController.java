package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.AgreementOfferResponse;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.service.RequestManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RequestManagementController {


    @Autowired
    private RequestManagementService requestManagementService;


    @GetMapping("/get-all-offers-grouped")
    public ResponseEntity<CommonResponse<List<AgreementOfferResponse>>> getAllOffersGrouped() {
        // Fetch grouped offers from service
        List<AgreementOfferResponse> groupedOffers = requestManagementService.getAllOffersGrouped();

        // Build response
        CommonResponse<List<AgreementOfferResponse>> response = new CommonResponse<>();
        response.setSuccess(true);
        response.setMessage("Data retrieved successfully");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setData(groupedOffers);

        return ResponseEntity.ok(response);
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
