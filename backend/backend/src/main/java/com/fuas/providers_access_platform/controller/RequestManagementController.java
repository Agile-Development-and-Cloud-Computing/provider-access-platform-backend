package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import com.fuas.providers_access_platform.service.RequestManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-request")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<List<Map<String, Object>>>> getServiceRequests(@PathVariable Long userId) {
        CommonResponse<List<Map<String, Object>>> response = requestManagementService.getServiceRequestsForUser(userId);
        return ResponseEntity.ok(response);
    }
}
