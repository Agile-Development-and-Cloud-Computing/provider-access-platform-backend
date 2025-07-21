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

            // Fetching all cycles associated with provider
            String cycleStatusSql = "SELECT offer_cycle FROM role_offer WHERE provider_id = ?";
            List<String> cycleStatuses = jdbcTemplate.queryForList(cycleStatusSql, new Object[]{providerId}, String.class);

            logger.info("Fetching service requests for providerId: {} with cycleStatuses: {}", providerId, cycleStatuses);

            ResponseEntity<String> response1 = restTemplate.getForEntity(API_URL_1, String.class);
            ResponseEntity<String> response2 = restTemplate.getForEntity(API_URL_2, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root1 = objectMapper.readTree(response1.getBody());
            JsonNode root2 = objectMapper.readTree(response2.getBody());

            List<LinkedHashMap<String, Object>> formattedRequests = new ArrayList<>();

            logger.info("Processing first API response");
            if (root1.isArray()) {
                processServiceRequests(root1, cycleStatuses, providerId, formattedRequests);
            }

            logger.info("Processing second API response");
            if (root2.isArray()) {
                processServiceRequests(root2, cycleStatuses, providerId, formattedRequests);
            }

            logger.info("Total requests processed: {}", formattedRequests.size());
            return ResponseEntity.ok(formattedRequests);

        } catch (Exception e) {
            logger.error("Error fetching service requests", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Make this method non-static
    private void processServiceRequests(JsonNode serviceRequests, List<String> cycleStatuses, Long providerId, List<LinkedHashMap<String, Object>> formattedRequests) {
        for (JsonNode request : serviceRequests) {
            LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();

            // Extract cycle status from the service request
            String requestCycleStatus = getField(request, "cycleStatus", "cycle") != null ? getField(request, "cycleStatus", "cycle").toString().trim().toLowerCase()
                    : "";
            // Validate if the request cycle matches any of the provider's cycle statuses
            List<String> lowerCaseCycleStatuses = cycleStatuses.stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList());

            if (!lowerCaseCycleStatuses.contains(requestCycleStatus)) {
                continue;
            }

            // Other fields extraction and processing
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

            // Iterate through selected members
            if (selectedMembers != null && selectedMembers.isArray()) {
                for (JsonNode member : selectedMembers) {
                    String domainName = getField(member, "domainName", "domainName").toString().trim();
                    String roleName = getField(member, "role", "role").toString().trim();
                    String level = getField(member, "level", "level").toString().trim();
                    String technologyLevel = getField(member, "technologyLevel", "technologyLevel").toString().trim();

                    // Query to get provider's roles and cycles matching role name, level, and technology level
                    String roleOfferSql = "SELECT * FROM role_offer WHERE provider_id = ? AND domain_name = ? AND LOWER(role_name) = LOWER(?) AND LOWER(experience_level) = LOWER(?) AND LOWER(technologies_catalog) = LOWER(?) AND offer_cycle = ?";
                    List<Map<String, Object>> roleOffers = jdbcTemplate.queryForList(roleOfferSql, providerId, domainName, roleName, level, technologyLevel, requestCycleStatus);

                    // If the role offer exists for the provider and matches all criteria
                    if (!roleOffers.isEmpty()) {
                        LinkedHashMap<String, Object> memberMap = new LinkedHashMap<>();
                        memberMap.put("domainId", member.has("domainId") ? member.get("domainId").asInt() : "NA");
                        memberMap.put("domainName", domainName);
                        memberMap.put("role", roleName);
                        memberMap.put("level", level);
                        memberMap.put("technologyLevel", technologyLevel);
                        memberMap.put("numberOfEmployee", getField(member, "numberOfEmployee", "numberOfProfilesNeeded"));
                        memberMap.put("_id", member.has("_id") ? member.get("_id").asText() : member.get("userID").asText());
                        selectedMembersList.add(memberMap);
                    }
                }
            }

            requestMap.put("selectedMembers", selectedMembersList);
            requestMap.put("representatives", getListFromField(request, "representatives", "representatives"));
            requestMap.put("notifications", getListFromField(request, "notifications", "notifications"));
            requestMap.put("createdBy", request.has("createdBy") ? request.get("createdBy").asText() : "Unknown");

            formattedRequests.add(requestMap);
        }
    }

    // Helper method to get fields from JSON node
    private Object getField(JsonNode node, String fieldName, String alias) {
        return node.has(fieldName) ? node.get(fieldName).asText() : node.has(alias) ? node.get(alias).asText() : null;
    }

    // Helper method to get a list of objects from JSON node
    private List<Object> getListFromField(JsonNode node, String fieldName, String alias) {
        List<Object> list = new ArrayList<>();
        JsonNode field = node.has(fieldName) ? node.get(fieldName) : node.has(alias) ? node.get(alias) : null;
        if (field != null && field.isArray()) {
            for (JsonNode element : field) {
                list.add(element.asText());
            }
        }
        return list;
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
