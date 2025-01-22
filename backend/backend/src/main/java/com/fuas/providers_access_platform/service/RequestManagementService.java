package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import com.fuas.providers_access_platform.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.sql.Date;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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


    public CommonResponse<List<Map<String,Object>>> getServiceRequestsOffers() {
        String sql = "SELECT " +
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
                "sr.is_approved, " +
                "so.offer_id, " +
                "so.provider_name, " +
                "so.employee_id, " +
                "so.role, " +
                "so.level, " +
                "so.technology_level, " +
                "so.location_type, " +
                "so.domain_id, " +
                "so.domain_name " +
                "FROM service_request sr " +
                "LEFT JOIN service_offers so ON sr.request_id = so.request_id " +
                "WHERE sr.is_approved = 0";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        if (rows.isEmpty()) {
            return new CommonResponse<>(false, "No service requests found.", null);
        }

        Map<String, Map<String, Object>> serviceRequestsMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            String requestId = (String) row.get("request_id");

            Map<String, Object> serviceRequest = serviceRequestsMap.get(requestId);
            if (serviceRequest == null) {
                serviceRequest = new LinkedHashMap<>();
                serviceRequest.put("serviceRequestId",row.get("service_request_id"));
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
                serviceRequest.put("serviceOffers", new ArrayList<>());
                serviceRequestsMap.put(requestId, serviceRequest);
            }

            Map<String, Object> serviceOffer = new LinkedHashMap<>();
            serviceOffer.put("offerId", row.get("offer_id"));
            serviceOffer.put("providerName", row.get("provider_name"));
            serviceOffer.put("employeeID", row.get("employee_id"));
            serviceOffer.put("role", row.get("role"));
            serviceOffer.put("level", row.get("level"));
            serviceOffer.put("technologyLevel", row.get("technology_level"));
            serviceOffer.put("locationType", row.get("location_type"));
            serviceOffer.put("domainId", row.get("domain_id"));
            serviceOffer.put("domainName", row.get("domain_name"));

            List<Map<String, Object>> serviceOffers = (List<Map<String, Object>>) serviceRequest.get("serviceOffers");
            serviceOffers.add(serviceOffer);
        }

        List<Map<String, Object>> serviceRequestsList = new ArrayList<>(serviceRequestsMap.values());
        return new CommonResponse<>(true, "Service requests fetched successfully.", serviceRequestsList);
    }


    @Transactional
    public void processServiceRequest(ServiceRequest request) {
        // Insert into service_request table
        String requestSql = "INSERT INTO service_request (request_id, master_agreement_id, master_agreement_name, " +
                "task_description, request_type, project, start_date, end_date, cycle_status, " +
                "number_of_specialists, number_of_offers, is_approved) " +
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
                request.isApproved()
        );

        // Insert provider offers into service_offers table
        for (ServiceRequest.ServiceOffer offer : request.getServiceOffers()) {
            saveServiceOffer(offer, request.getRequestID());
        }
    }

    private void saveServiceOffer(ServiceRequest.ServiceOffer offer, String requestID) {
        String offerSql = "INSERT INTO service_offers (request_id, provider_id, provider_name, " +
                "employee_id, role, level, technology_level, location_type, domain_Id, domain_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                offer.getDomainName()
        );
    }

}


