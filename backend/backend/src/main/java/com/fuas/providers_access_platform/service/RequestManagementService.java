package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import com.fuas.providers_access_platform.model.Employee;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.repository.RoleOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


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


    public CommonResponse<List<Map<String,Object>>> getServiceRequestsForUser(Long userId) {
        // Fetch the cycle status of the user based on their user ID
        String cycleStatusSql = "SELECT cycle_status FROM user WHERE provider_id = ?";
        String cycleStatus = jdbcTemplate.queryForObject(cycleStatusSql, new Object[]{userId}, String.class);


        System.out.println("Before Query");
        String sql = "SELECT " +
                "sat.service_request_id, " +
                "sat.agreement_id, " +
                "sat.agreement_name, " +
                "sat.task_description, " +
                "sat.project, " +
                "sat.begin_date, " +
                "sat.end_date, " +
                "sat.amount_of_man_days, " +
                "sat.location, " +
                "sat.type, " +
                "sat.cycle_status, " +
                "sat.number_of_specialists, " +
                "sat.number_of_offers, " +
                "sat.consumer, " +
                "sat.location_type, " +
                "sat.information_for_provider_manager, " +
                "sat.notifications, " +
                "sr.domain_id, " +
                "sr.domain_name, " +
                "sr.role, " +
                "sr.level, " +
                "sr.technology_level " +
                "FROM service_agreement_types sat " +
                "LEFT JOIN service_request sr ON sr.service_request_id = sat.service_request_id " +
                "WHERE sat.cycle_status = ?";  // For example, 'cycle_one'

        // Query the database
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, cycleStatus);

        if (rows.isEmpty()) {
            return new CommonResponse<>( false, "No service requests found for the specified cycle status.", null);
        }
        // To hold the final response
        List<Map<String, Object>> serviceRequestsList = new ArrayList<>();

        // Map to group results by service request id
        Map<String, Map<String, Object>> serviceRequestsMap = new HashMap<>();

        // Process each row from the query result
        for (Map<String, Object> row : rows) {
            String serviceRequestId = (String) row.get("service_request_id");

            // Check if service request already exists in the map
            Map<String, Object> serviceRequest = serviceRequestsMap.get(serviceRequestId);
            if (serviceRequest == null) {
                serviceRequest = new HashMap<>();
                serviceRequest.put("serviceRequestId", serviceRequestId);
                serviceRequest.put("agreementId", row.get("agreement_id"));
                serviceRequest.put("agreementName", row.get("agreement_name"));
                serviceRequest.put("taskDescription", row.get("task_description"));
                serviceRequest.put("project", row.get("project"));
                serviceRequest.put("begin", row.get("begin_date"));
                serviceRequest.put("end", row.get("end_date"));
                serviceRequest.put("amountOfManDays", row.get("amount_of_man_days"));
                serviceRequest.put("location", row.get("location"));
                serviceRequest.put("type", row.get("type"));
                serviceRequest.put("cycleStatus", row.get("cycle_status"));
                serviceRequest.put("numberOfSpecialists", row.get("number_of_specialists"));
                serviceRequest.put("numberOfOffers", row.get("number_of_offers"));
                serviceRequest.put("consumer", row.get("consumer"));
                serviceRequest.put("locationType", row.get("location_type"));
                serviceRequest.put("informationForProviderManager", row.get("information_for_provider_manager"));
                serviceRequest.put("notifications", row.get("notifications"));

                // Initialize the selectedMembers list
                serviceRequest.put("selectedMembers", new ArrayList<Map<String, Object>>());

                // Add to the map of service requests
                serviceRequestsMap.put(serviceRequestId, serviceRequest);
            }

            // Extract member data
            Map<String, Object> member = new HashMap<>();
            member.put("domainId", row.get("domain_id"));
            member.put("domainName", row.get("domain_name"));
            member.put("role", row.get("role"));
            member.put("level", row.get("level"));
            member.put("technologyLevel", row.get("technology_level"));

            // Add the member to the selectedMembers list
            List<Map<String, Object>> selectedMembers = (List<Map<String, Object>>) serviceRequest.get("selectedMembers");
            selectedMembers.add(member);
        }

        // Convert the map values to the final list of results
        serviceRequestsList.addAll(serviceRequestsMap.values());
        return new CommonResponse<>(true, "Service requests fetched successfully.", serviceRequestsList);
    }
}


