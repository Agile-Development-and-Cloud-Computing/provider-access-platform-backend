package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.model.Domain;
import com.fuas.providers_access_platform.model.RoleOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
            String OfferSql = "INSERT INTO offer " +
                    "(domain_id, domain_name, role_name, experience_level, " +
                    "technologies_catalog, quote_price, offer_date, master_agreement_type_name, status) " +    //quote price comes from Group 1
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? )";
            for (Domain domain : request.getDomains()) {
                for (RoleOffer roleOffers : domain.getRoleOffer()) {
                    jdbcTemplate.update(OfferSql,
                            domain.getDomainId(),
                            domain.getDomainName(),
                            roleOffers.getRoleName(),
                            roleOffers.getExperienceLevel(),
                            roleOffers.getTechnologiesCatalog(),
                            roleOffers.getQuotePrice(),
                            request.getCreatedAt(),
                            request.getMasterAgreementTypeName(),
                            request.getStatus()
                    );
                }
            }

            return new CommonResponse(true, "Master Agreement successfully created", null);

        } catch (Exception e) {
            return new CommonResponse(false, "Error creating Master Agreement: " + e.getMessage(), null);
        }
    }


    @Transactional
    public boolean updateOffer(RoleOffer request) {

        try {
            // Log the request to ensure parameters are correctly passed
            System.out.println("Request Data: " +
                    "Domain ID: " + request.getDomainId() + ", " +
                    "Domain Name: " + request.getDomainName() + ", " +
                    "Role Name: " + request.getRoleName() + ", " +
                    "Master Agreement Type Name: " + request.getMasterAgreementTypeName() + ", " +
                    "Master Agreement Type ID: " + request.getMasterAgreementTypeId());

            // SQL query to select data from the 'offer' table based on the provided parameters
            String selectSql = "SELECT domain_id, domain_name, role_name, experience_level, technologies_catalog, quote_price, offer_date, master_agreement_type_name, status " +
                    "FROM offer " +
                    "WHERE domain_id = ? " +
                    "AND domain_name = ? " +
                    "AND role_name = ? " +
                    "AND master_agreement_type_name = ?";

            // Fetch the data from the 'offer' table based on the request
            List<Map<String, Object>> offers = jdbcTemplate.queryForList(selectSql,
                    request.getDomainId(),
                    request.getDomainName(),
                    request.getRoleName(),
                    request.getMasterAgreementTypeName()
            );

            // Check if the query result is empty
            if (offers.isEmpty()) {
                return false;
            }

            // SQL query to insert into role_offers
            String insertSql = "INSERT INTO role_offer (id, role_name, experience_level, technologies_catalog, domain_name, domain_id, quote_price, master_agreement_type_id, " +
                    "master_agreement_type_name, provider, bid_price ) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

            // Loop through the offers retrieved from the 'offer' table
            for (Map<String, Object> offer : offers) {
                jdbcTemplate.update(insertSql,
                        request.getId(),
                        offer.get("role_name"),  // role_name
                        offer.get("experience_level"),  // experience_level
                        offer.get("technologies_catalog"),  // technologies_catalog
                        offer.get("domain_name"),  // domain_name
                        offer.get("domain_id"),// domain_id
                        offer.get("quote_price"),
                        request.getMasterAgreementTypeId(),  // master_agreement_type_id from request
                        request.getMasterAgreementTypeName(),  // master_agreement_type_name from request
                        request.getProvider(),  // provider from request
                        request.getBidPrice() // quote_price from request

                );
            }

            return true;
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Error while updating offer: " + e.getMessage(), e);
        }
    }

}
