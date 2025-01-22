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

@RestController
@RequestMapping("/api/service-request")
@CrossOrigin(origins = "http://localhost:3000")
public class RequestManagementController {


    @Autowired
    private RequestManagementService requestManagementService;


    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;

    public RequestManagementController (RestTemplate restTemplate, JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String API_URL_1 = "https://service-management-backend-production.up.railway.app/api/service-requests/published";
    private final String API_URL_2 = "https://servicerequestapi-d0g3ezftcggucbev.germanywestcentral-01.azurewebsites.net/api/ServiceRequest/ServiceRequestList";


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
            RestTemplate restTemplate = new RestTemplate();
            String cycleStatusSql = "SELECT cycle_status FROM user WHERE provider_id = ?";
            String cycleStatus = jdbcTemplate.queryForObject(cycleStatusSql, new Object[]{providerId}, String.class);

            logger.info("Fetching service requests for providerId: {} with cycleStatus: {}", providerId, cycleStatus);

            ResponseEntity<String> response1 = restTemplate.getForEntity(API_URL_1, String.class);
            ResponseEntity<String> response2 = restTemplate.getForEntity(API_URL_2, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root1 = objectMapper.readTree(response1.getBody());
            JsonNode root2 = objectMapper.readTree(response2.getBody());

            List<LinkedHashMap<String, Object>> formattedRequests = new ArrayList<>();

            logger.info("Processing first API response");
            if (root1.isArray()) {
                processServiceRequests(root1, cycleStatus, formattedRequests);
            }

            logger.info("Processing second API response");
            if (root2.isArray()) {
                processServiceRequests(root2, cycleStatus, formattedRequests);
            }

            logger.info("Total requests processed: {}", formattedRequests.size());
            return ResponseEntity.ok(formattedRequests);

        } catch (Exception e) {
            logger.error("Error fetching service requests", e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    private static void processServiceRequests(JsonNode serviceRequests, String cycleStatus, List<LinkedHashMap<String, Object>> formattedRequests) {
        for (JsonNode request : serviceRequests) {
            LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();

            String requestCycleStatus = getField(request, "cycleStatus", "cycle").toString();
            if (!requestCycleStatus.equals(cycleStatus)) {
                continue;
            }

            requestMap.put("ServiceRequestId", getField(request, "ServiceRequestId", "requestID"));
            requestMap.put("agreementId", getField(request, "agreementId", "masterAgreementID"));
            requestMap.put("agreementName", getField(request, "agreementName", "masterAgreementName"));
            requestMap.put("taskDescription", getField(request, "taskDescription", "taskDescription"));
            requestMap.put("project", getField(request, "project", "project"));
            requestMap.put("begin", getField(request, "begin", "startDate"));
            requestMap.put("end", getField(request, "end", "endDate"));
            requestMap.put("amountOfManDays", getField(request, "amountOfManDays", "totalManDays"));
            requestMap.put("location", getField(request, "location", "location"));
            requestMap.put("type", getField(request, "type", "requestType"));
            requestMap.put("cycleStatus", getField(request, "cycleStatus", "cycle"));
            requestMap.put("numberOfSpecialists", getField(request, "numberOfSpecialists"));
            requestMap.put("consumer", getField(request, "consumer", "consumer"));
            requestMap.put("informationForProviderManager", getField(request, "informationForProviderManager", "providerManagerInfo"));
            requestMap.put("locationType", getField(request, "locationType", "locationType"));
            requestMap.put("numberOfOffers", getField(request, "numberOfOffers"));

            List<LinkedHashMap<String, Object>> selectedMembersList = new ArrayList<>();
            JsonNode selectedMembers = request.has("selectedMembers") ? request.get("selectedMembers") : request.get("roleSpecific");

            if (selectedMembers != null && selectedMembers.isArray()) {
                for (JsonNode member : selectedMembers) {
                    LinkedHashMap<String, Object> memberMap = new LinkedHashMap<>();
                    memberMap.put("domainId", member.has("domainId") ? member.get("domainId").asInt() : "NA");
                    memberMap.put("domainName", member.has("domainName") ? member.get("domainName").asText() :
                            (member.has("selectedDomainName") ? member.get("selectedDomainName").asText() : "NA"));
                    memberMap.put("role", getField(member, "role", "role"));
                    memberMap.put("level", getField(member, "level", "level"));
                    memberMap.put("technologyLevel", getField(member, "technologyLevel", "technologyLevel"));
                    memberMap.put("numberOfEmployee", getField(member, "numberOfEmployee", "numberOfProfilesNeeded"));
                    memberMap.put("_id", member.has("_id") ? member.get("_id").asText() : member.get("userID").asText());
                    selectedMembersList.add(memberMap);
                }
            }

            requestMap.put("selectedMembers", selectedMembersList);
            requestMap.put("representatives", getListFromField(request, "representatives", "representatives"));
            requestMap.put("notifications", getListFromField(request, "notifications", "notifications"));
            requestMap.put("createdBy", request.has("createdBy") ? request.get("createdBy").asText() : "Unknown");
            System.out.println("CreatedBy: " + request.get("createdBy"));

            formattedRequests.add(requestMap);
        }
    }

    private static Object getField(JsonNode node, String... possibleKeys) {
        for (String key : possibleKeys) {
            if (node.has(key)) {
                if (node.get(key).isTextual()) {
                    return node.get(key).asText();
                } else if (node.get(key).isNumber()) {
                    return node.get(key).asInt();
                } else if (node.get(key).isArray()) {
                    return node.get(key);
                }
            }
        }
        return null;
    }

    private static List<String> getListFromField(JsonNode node, String... possibleKeys) {
        for (String key : possibleKeys) {
            if (node.has(key) && node.get(key).isArray()) {
                List<String> list = new ArrayList<>();
                for (JsonNode item : node.get(key)) {
                    list.add(item.asText());
                }
                return list;
            }
        }
        return Collections.emptyList();
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


}
