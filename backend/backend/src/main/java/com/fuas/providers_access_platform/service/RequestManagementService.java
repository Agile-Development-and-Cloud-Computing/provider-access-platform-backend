package com.fuas.providers_access_platform.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import com.fuas.providers_access_platform.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.sql.Date;
import java.util.stream.Collectors;


@Service
public class RequestManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL_1 = "https://service-management-backend-production.up.railway.app/api/service-requests/published";
    private static final String API_URL_2 = "https://servicerequestapi-d0g3ezftcggucbev.germanywestcentral-01.azurewebsites.net/api/ServiceRequest/ServiceRequestList";


    public CommonResponse placeBid(BidRequest bidRequest) {
        try {
            String query = "INSERT INTO bids (service_id, provider_id, bid_amount) VALUES (?, ?, ?)";
            jdbcTemplate.update(query, bidRequest.getServiceId(), bidRequest.getProviderId(), bidRequest.getBidAmount());
            return new CommonResponse<>(true, "Bid placed successfully", null);
        } catch (Exception e) {
            return new CommonResponse<>(false, "Failed to place bid: " + e.getMessage(), null);
        }
    }


    public CommonResponse acceptOrder(int serviceRequestId, int employeeId) {
        try {

            // Check if the service request exists and is open (not already accepted)
            String checkRequestQuery = "SELECT status FROM service_requests WHERE service_id = ?";
            String status = jdbcTemplate.queryForObject(checkRequestQuery, new Object[]{serviceRequestId}, String.class);

            if ("accepted".equalsIgnoreCase(status)) {
                return new CommonResponse(false, "Service request already accepted", null);
            }

            // Verify the employee exists and is available
            String checkEmployeeQuery = "SELECT employee_id, employee_name FROM employees WHERE employee_id = ?";
            Employee employee = jdbcTemplate.queryForObject(checkEmployeeQuery, new Object[]{employeeId}, (rs, rowNum) ->
                    new Employee(rs.getInt("employee_id"), rs.getString("employee_name"))
            );

            // Update the order status and assign the employee profile to the service request
            String updateOrderQuery = "UPDATE service_requests SET status = 'accepted', employee_id = ? WHERE service_id = ?";
            jdbcTemplate.update(updateOrderQuery, employeeId, serviceRequestId);

            // Create a response with employee details
            // Prepare a map with employee details (only employee_id and employee_name)
            Map<String, Object> employeeData = new HashMap<>();
            employeeData.put("employeeId", employee.getEmployeeId());
            employeeData.put("employeeName", employee.getEmployeeName());
            return new CommonResponse(true, "Order accepted successfully", employeeData);
        } catch (Exception e) {
            // Handle exceptions
            throw new RuntimeException("Error accepting the order", e);
        }
    }


    public CommonResponse<List<Map<String, Object>>> getServiceRequestsOffers() {
        String sql = "SELECT DISTINCT " +
                "sr.id AS service_request_id, " +
                "sr.request_id, " +
                "sr.master_agreement_id, " +
                "sr.master_agreement_name, " +
                "sr.task_description, " +
                "sr.request_type, " +
                "sr.project, " +
                "sr.start_date, " +
                "sr.end_date, " +
                "sr.cycle_status, " +
                "sr.number_of_specialists, " +
                "sr.number_of_offers, " +
                "sr.created_by, " +
                "so.offer_id, " +
                "so.provider_name, " +
                "so.provider_id, " +
                "so.employee_id, " +
                "so.role, " +
                "so.level, " +
                "so.technology_level, " +
                "so.location_type, " +
                "so.domain_id, " +
                "so.domain_name, " +
                "so.user_id, " +
                "so.is_approved, " +
                "ro.bid_price " +
                "FROM service_request sr " +
                "LEFT JOIN service_offers so ON sr.request_id = so.request_id " +
                "LEFT JOIN role_offer ro ON so.provider_id = ro.provider_id " +
                "AND so.role = ro.role_name " +
                "AND so.level = ro.experience_level " +
                "AND so.technology_level = ro.technologies_catalog " +
                "AND sr.master_agreement_id = ro.master_agreement_type_id " +
                "WHERE so.is_approved = 0";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        if (rows.isEmpty()) {
            return new CommonResponse<>(false, "No service requests found.", null);
        }

        // Use a map to group service requests by request_id
        Map<String, Map<String, Object>> serviceRequestsMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            String requestId = (String) row.get("request_id");

            // Get or create service request map
            Map<String, Object> serviceRequest = serviceRequestsMap.get(requestId);
            if (serviceRequest == null) {
                serviceRequest = new LinkedHashMap<>();
                serviceRequest.put("serviceRequestId", row.get("service_request_id"));
                serviceRequest.put("requestID", requestId);
                serviceRequest.put("masterAgreementID", row.get("master_agreement_id"));
                serviceRequest.put("masterAgreementName", row.get("master_agreement_name"));
                serviceRequest.put("taskDescription", row.get("task_description"));
                serviceRequest.put("requestType", row.get("request_type"));
                serviceRequest.put("project", row.get("project"));
                serviceRequest.put("startDate", row.get("start_date"));
                serviceRequest.put("endDate", row.get("end_date"));
                serviceRequest.put("cycleStatus", row.get("cycle_status"));
                serviceRequest.put("numberOfSpecialists", row.get("number_of_specialists"));
                serviceRequest.put("numberOfOffers", row.get("number_of_offers"));
                serviceRequest.put("isApproved", row.get("is_approved"));
                serviceRequest.put("createdBy", row.get("created_by"));
                serviceRequest.put("serviceOffers", new ArrayList<Map<String, Object>>());
                serviceRequestsMap.put(requestId, serviceRequest);
            }

            // Prepare service offer map
            Map<String, Object> serviceOffer = new LinkedHashMap<>();
            serviceOffer.put("offerId", row.get("offer_id"));
            serviceOffer.put("providerName", row.get("provider_name"));
            serviceOffer.put("providerId", row.get("provider_id"));
            serviceOffer.put("employeeID", row.get("employee_id"));
            serviceOffer.put("role", row.get("role"));
            serviceOffer.put("level", row.get("level"));
            serviceOffer.put("technologyLevel", row.get("technology_level"));
            serviceOffer.put("domainId", row.get("domain_id"));
            serviceOffer.put("domainName", row.get("domain_name"));
            serviceOffer.put("userId", row.get("user_id"));
            serviceOffer.put("price", row.get("bid_price"));

            // Add service offer only if it doesn't already exist
            List<Map<String, Object>> serviceOffers = (List<Map<String, Object>>) serviceRequest.get("serviceOffers");
            boolean exists = serviceOffers.stream().anyMatch(offer -> offer.get("offerId").equals(row.get("offer_id")));

            if (!exists) {
                serviceOffers.add(serviceOffer);
            }
        }

        // Convert map to list of service requests
        List<Map<String, Object>> serviceRequestsList = new ArrayList<>(serviceRequestsMap.values());

        return new CommonResponse<>(true, "Service requests fetched successfully.", serviceRequestsList);
    }



    @Transactional
    public void processServiceRequest(ServiceRequest request) {
        // Insert into service_request table
        String requestSql = "INSERT INTO service_request (request_id, master_agreement_id, master_agreement_name, " +
                "task_description, request_type, project, start_date, end_date, cycle_status, " +
                "number_of_specialists, number_of_offers, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(requestSql,
                request.getRequestID(),
                request.getMasterAgreementID(),
                request.getMasterAgreementName(),
                request.getTaskDescription(),
                request.getRequestType(),
                request.getProject(),
                Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()),
                request.getCycleStatus(),
                request.getNumberOfSpecialists(),
                request.getNumberOfOffers(),
                request.getCreatedBy()
        );

        // Insert provider offers into service_offers table
        for (ServiceRequest.ServiceOffer offer : request.getServiceOffers()) {
            saveServiceOffer(offer, request.getRequestID());
        }
    }

    private void saveServiceOffer(ServiceRequest.ServiceOffer offer, String requestID) {
        String offerSql = "INSERT INTO service_offers (request_id, provider_id, provider_name, " +
                "employee_id, role, level, technology_level, location_type, domain_Id, domain_name, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(offerSql,
                requestID,
                offer.getProviderID(),
                offer.getProviderName(),
                offer.getEmployeeID(),
                offer.getRole(),
                offer.getLevel(),
                offer.getTechnologyLevel(),
                offer.getLocationType(),
                offer.getDomainId(),
                offer.getDomainName(),
                offer.getUserId()
        );
    }

    public String updateOfferStatus(Long offerId, String requestId, String status, String comments) {
        // Validate status input

        System.out.println("Received request to update offer status");
        System.out.println("Offer ID: " + offerId);
        System.out.println("Request ID: " + requestId);
        System.out.println("Status: " + status);
        System.out.println("Comments: " + comments);
        if (!"Accepted".equalsIgnoreCase(status) && !"Declined".equalsIgnoreCase(status)) {
            return "Invalid status value. Use 'Accepted' or 'Declined'.";
        }

        // SQL query to update the offer status and comments
        String sql = "UPDATE service_offers SET is_approved = ?, comments = ? " +
                "WHERE offer_id = ? AND request_id = ?";

        try {
            // Execute the update
            int rowsAffected = jdbcTemplate.update(sql, status, comments,offerId, requestId);

            // Check if the update was successful
            if (rowsAffected > 0) {
                return "Offer status updated successfully";
            } else {
                return "No rows updated. Please check if the offer_id and request_id are correct.";
            }
        } catch (Exception e) {
            // Log and return the error message
            return "Error updating offer status: " + e.getMessage();
        }
    }

    public List<LinkedHashMap<String, Object>> getServiceRequests(Long providerId) throws Exception {
        // 1. Get provider's cycle statuses
        String cycleStatusSql = "SELECT offer_cycle FROM role_offer WHERE provider_id = ?";
        List<String> cycleStatuses = jdbcTemplate.queryForList(cycleStatusSql, new Object[]{providerId}, String.class);

        // 2. Call external APIs
        ResponseEntity<String> response1 = restTemplate.getForEntity(API_URL_1, String.class);
        ResponseEntity<String> response2 = restTemplate.getForEntity(API_URL_2, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root1 = objectMapper.readTree(response1.getBody());
        JsonNode root2 = objectMapper.readTree(response2.getBody());

        List<LinkedHashMap<String, Object>> formattedRequests = new ArrayList<>();
        if (root1.isArray()) {
            processServiceRequests(root1, cycleStatuses, providerId, formattedRequests);
        }
        if (root2.isArray()) {
            processServiceRequests(root2, cycleStatuses, providerId, formattedRequests);
        }

        return formattedRequests;
    }

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
            requestMap.put("numberOfSpecialists", getField(request, "numberOfSpecialists","numberOfSpecialists"));
            requestMap.put("consumer", getField(request, "consumer", "consumer"));
            requestMap.put("informationForProviderManager", getField(request, "informationForProviderManager", "providerManagerInfo"));
            requestMap.put("locationType", getField(request, "locationType", "locationType"));
            requestMap.put("numberOfOffers", getField(request, "numberOfOffers","numberOfOffers"));

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

    private Object getField(JsonNode node, String fieldName, String alias) {
        return node.has(fieldName) ? node.get(fieldName).asText() : node.has(alias) ? node.get(alias).asText() : null;
    }

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

}


