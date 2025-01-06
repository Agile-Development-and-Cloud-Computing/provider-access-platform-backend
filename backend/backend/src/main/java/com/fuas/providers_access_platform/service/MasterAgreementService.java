package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MasterAgreementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getMasterAgreementsWithRoleOffer() {
        String sql = "SELECT " +
                "    ma.master_agreement_type_id, " +
                "    ma.master_agreement_type_name, " +
                "    ma.valid_from, " +
                "    ma.valid_until, " +
                "    ro.role_name, " +
                "    ro.experience_level, " +
                "    ro.technologies_catalog, " +
                "    ro.domain_id, " +
                "    ro.domain_name, " +
                "    ro.offer_cycle, " +
                "    ro.provider, " +
                "    ro.quote_price, " +
                "    ro.is_accepted " +
                " FROM master_agreement_types ma " +
                " INNER JOIN role_offer ro " +
                "    ON ma.master_agreement_type_id = ro.master_agreement_type_id " +
                " WHERE ro.is_accepted = 1;"; // Example condition to fetch only accepted offers

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("masterAgreementTypeId",rs.getInt("master_agreement_type_id"));
            response.put("masterAgreementTypeName",rs.getString("master_agreement_type_name"));
            response.put("validFrom",rs.getDate("valid_from"));
            response.put("validUntil",rs.getDate("valid_until"));
            response.put("roleName",rs.getString("role_name"));
            response.put("experienceLevel",rs.getString("experience_level"));
            response.put("technologiesCatalog",rs.getString("technologies_catalog"));
            response.put("domainId",rs.getInt("domain_id"));
            response.put("domainName",rs.getString("domain_name"));
            response.put("offerCycle",rs.getString("offer_cycle"));
            response.put("provider",rs.getString("provider"));
            response.put("quotePrice",rs.getBigDecimal("quote_price"));
            response.put("isAccepted",rs.getBoolean("is_accepted"));
            return response;
        });
    }

    public CommonResponse createOffer(MasterAgreementRequest masterAgreementRequest) {
        // Validate input (you can add more validation as needed)
        if (masterAgreementRequest.getMasterAgreementTypeId() == null || masterAgreementRequest.getProvider() == null) {
            return new CommonResponse(false, "Invalid data", null);
        }

        // Construct the SQL query for inserting an offer
        String sql = "INSERT INTO role_offer (master_agreement_type_id, master_agreement_type_name, " +
                "role_name, experience_level, technologies_catalog, domain_id, domain_name, offer_cycle, " +
                "provider, quote_price, is_accepted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Execute the query to insert data
            jdbcTemplate.update(sql,
                    masterAgreementRequest.getMasterAgreementTypeId(),
                    masterAgreementRequest.getMasterAgreementTypeName(),
                    masterAgreementRequest.getRoleName(),
                    masterAgreementRequest.getExperienceLevel(),
                    masterAgreementRequest.getTechnologiesCatalog(),
                    masterAgreementRequest.getDomainId(),
                    masterAgreementRequest.getDomainName(),
                    masterAgreementRequest.getOfferCycle(),
                    masterAgreementRequest.getProvider(),
                    masterAgreementRequest.getQuotePrice(),
                    false // Initially, the offer is not accepted
            );
            // Return success response
            return new CommonResponse(true, "Offer successfully created", null);
        } catch (Exception e) {
            // Handle any errors during the DB operation
            return new CommonResponse(false, "Error creating the offer: " + e.getMessage(), null);
        }
    }
}
