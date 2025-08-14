package com.fuas.providers_access_platform.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import com.fuas.providers_access_platform.service.RequestManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/service-request")
public class RequestManagementController {


    @Autowired
    private RequestManagementService requestManagementService;

    @PostMapping("/bid/place")
    public ResponseEntity<CommonResponse> placeBid(@RequestBody BidRequest bidRequest) {
        CommonResponse response = requestManagementService.placeBid(bidRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/accept-order")
    public ResponseEntity<CommonResponse> acceptOrder(@RequestBody BidRequest request) {
        try {
            // Call the service to process the order acceptance
            CommonResponse response = requestManagementService.acceptOrder(request.getServiceId(), request.getEmployeeId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse(false, "Error accepting the order", null));
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<CommonResponse<List<Map<String, Object>>>> getServiceRequestsOffers() {
        CommonResponse<List<Map<String, Object>>> response = requestManagementService.getServiceRequestsOffers();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/published/{providerId}")
    public ResponseEntity<List<LinkedHashMap<String, Object>>> getServiceRequest(@PathVariable Long providerId) {
        Logger logger = LoggerFactory.getLogger(getClass());
        try {
            List<LinkedHashMap<String, Object>> result = requestManagementService.getServiceRequests(providerId);
            logger.info("Total requests processed: {}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching service requests", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitServiceRequest(@RequestBody ServiceRequest request) {
        try {
            requestManagementService.processServiceRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Service request submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting request: " + e.getMessage());
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateOfferStatus(@RequestBody ServiceRequest offerUpdateRequest) {
        // Call the service to update the status

        System.out.println("Received request to update offer status");
        System.out.println("Offer ID: " + offerUpdateRequest.getId());
        System.out.println("Request ID: " + offerUpdateRequest.getRequestID());
        System.out.println("Status: " + offerUpdateRequest.getIsApproved());
        System.out.println("Comments: " + offerUpdateRequest.getComments());

        String responseMessage = requestManagementService.updateOfferStatus(
                offerUpdateRequest.getId(),
                offerUpdateRequest.getRequestID(),
                offerUpdateRequest.getIsApproved(),
                offerUpdateRequest.getComments()
        );

        // If the message contains 'successfully', return success status
        if (responseMessage.contains("successfully")) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", responseMessage
            ));
        } else {
            // Return failure status with the message
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "failure",
                    "message", responseMessage
            ));
        }
    }


}
