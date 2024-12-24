package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.dto.MasterAgreementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterAgreementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<MasterAgreementResponse> getMasterAgreementsWithRoleOffer() {
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
            MasterAgreementResponse masterAgreement = new MasterAgreementResponse();
            masterAgreement.setMasterAgreementTypeId(rs.getInt("master_agreement_type_id"));
            masterAgreement.setMasterAgreementTypeName(rs.getString("master_agreement_type_name"));
            masterAgreement.setValidFrom(rs.getDate("valid_from"));
            masterAgreement.setValidUntil(rs.getDate("valid_until"));
            masterAgreement.setRoleName(rs.getString("role_name"));
            masterAgreement.setExperienceLevel(rs.getString("experience_level"));
            masterAgreement.setTechnologiesCatalog(rs.getString("technologies_catalog"));
            masterAgreement.setDomainId(rs.getInt("domain_id"));
            masterAgreement.setDomainName(rs.getString("domain_name"));
            masterAgreement.setOfferCycle(rs.getString("offer_cycle"));
            masterAgreement.setProvider(rs.getString("provider"));
            masterAgreement.setQuotePrice(rs.getBigDecimal("quote_price"));
            masterAgreement.setIsAccepted(rs.getBoolean("is_accepted"));
            return masterAgreement;
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
