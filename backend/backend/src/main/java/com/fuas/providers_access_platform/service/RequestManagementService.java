package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.BidRequest;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.model.Employee;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.repository.RoleOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RequestManagementService {

    @Autowired
    private RoleOfferRepository roleOfferRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String,Object>> getAllOffersGrouped() {
        // Fetch all role offers from the database
        List<RoleOffer> roleOffers = roleOfferRepository.findAll();

        System.out.println("Rows"+roleOffers.toString());
        // Group by roleName, masterAgreementTypeName, and domainId
        Map<String, List<RoleOffer>> groupedOffers = roleOffers.stream().collect(Collectors.groupingBy(
                offer -> offer.getRoleName() + "|" + offer.getMasterAgreementTypeName() + "|" + offer.getDomainId()
        ));

        System.out.println("Rows"+ groupedOffers.toString());
        // Transform grouped data into List<Map<String, Object>>
        return groupedOffers.values().stream().map(offers -> {
            RoleOffer firstOffer = offers.get(0);

            // Create a map for the main response object
            Map<String, Object> response = new HashMap<>();
            response.put("roleName", firstOffer.getRoleName());
            response.put("experienceLevel", firstOffer.getExperienceLevel());
            response.put("technologiesCatalog", firstOffer.getTechnologiesCatalog());
            response.put("domainId", firstOffer.getDomainId());
            response.put("domainName", firstOffer.getDomainName());
            response.put("masterAgreementTypeId", firstOffer.getMasterAgreementTypeId());
            response.put("masterAgreementTypeName", firstOffer.getMasterAgreementTypeName());

            // Map providers as a list of maps
            List<Map<String, Object>> providers = offers.stream().map(offer -> {
                Map<String, Object> provider = new HashMap<>();
                provider.put("offerId", offer.getId());
                provider.put("providerName", offer.getProvider());
                provider.put("quotePrice", offer.getQuotePrice());
                provider.put("isAccepted", offer.getIsAccepted());
                provider.put("offerCycle", offer.getOfferCycle());
                return provider;
            }).collect(Collectors.toList());

            response.put("providers", providers);
            return response;
        }).collect(Collectors.toList());
    }

    public void updateOfferResponse(Long offerId, Boolean isAccepted) {
        // Fetch the record by offerId
        RoleOffer offer = roleOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer with ID " + offerId + " not found."));

        // Update the isAccepted field
        offer.setIsAccepted(isAccepted);

        // Save the updated offer back to the database
        roleOfferRepository.save(offer);
    }

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
}
