package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.model.Domain;
import com.fuas.providers_access_platform.model.RoleOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CommonResponse createMasterAgreementOffer(MasterAgreementRequest request) {
        try {
            // Insert into master_agreement_types table
            String masterAgreementSql = "INSERT INTO master_agreement_types " +
                    "(master_agreement_type_id, master_agreement_type_name, valid_from, valid_until, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(
                    masterAgreementSql,
                    request.getMasterAgreementTypeId(),
                    request.getMasterAgreementTypeName(),
                    request.getValidFrom(),
                    request.getValidUntil(),
                    request.getStatus(),
                    request.getCreatedAt()
            );

            // Loop through the domains and insert into role_offer table
            String roleOfferSql = "INSERT INTO role_offer" +
                    "(id, domain_id ,master_agreement_type_id, master_agreement_type_name ,domain_name ,role_name, experience_level," +
                    "technologies_catalog, provider, quote_price, offer_cycle) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            for (Domain domain : request.getDomains()) {
                for (RoleOffer roleOffers : domain.getRoleOffer()) {
                    jdbcTemplate.update(roleOfferSql,
                            roleOffers.getId(),
                            domain.getDomainId(),
                            request.getMasterAgreementTypeId(),
                            request.getMasterAgreementTypeName(),
                            domain.getDomainName(),
                            roleOffers.getRoleName(),
                            roleOffers.getExperienceLevel(),
                            roleOffers.getTechnologiesCatalog(),
                            roleOffers.getProvider(),
                            roleOffers.getQuotePrice(),
                            roleOffers.getOfferCycle()
                    );
                }
            }

            return new CommonResponse(true, "Master Agreement and roles successfully created", null);

        } catch (Exception e) {
            return new CommonResponse(false, "Error creating Master Agreement: " + e.getMessage(), null);
        }
    }
}
