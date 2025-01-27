package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.model.Domain;
import com.fuas.providers_access_platform.model.RoleOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MasterAgreementService {

    private static final Logger logger = LoggerFactory.getLogger(MasterAgreementService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getMasterAgreementsWithRoleOffer() {
        String sql = "SELECT " +
                "    ma.master_agreement_type_id, " +
                "    ma.master_agreement_type_name, " +
                "    ma.valid_from, " +
                "    ma.valid_until, " +
                "    ma.created_at, " +
                "    ro.role_id, " +
                "    ro.role_name, " +
                "    ro.experience_level, " +
                "    ro.technologies_catalog, " +
                "    ro.domain_id, " +
                "    ro.domain_name, " +
                "    ro.quote_price, " +
                "    ro.is_accepted " +
                " FROM master_agreement_types ma " +
                " INNER JOIN offer ro " +
                "    ON ma.master_agreement_type_id = ro.master_agreement_type_id " +
                " WHERE ro.is_accepted = 0;";

        logger.info("Executing SQL query to fetch master agreements and role offers: {}", sql);

        List<Map<String, Object>> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("masterAgreementTypeId", rs.getInt("master_agreement_type_id"));
            response.put("masterAgreementTypeName", rs.getString("master_agreement_type_name"));
            response.put("validFrom", rs.getDate("valid_from"));
            response.put("validUntil", rs.getDate("valid_until"));
            response.put("roleId", rs.getString("role_id"));
            response.put("roleName", rs.getString("role_name"));
            response.put("experienceLevel", rs.getString("experience_level"));
            response.put("technologiesCatalog", rs.getString("technologies_catalog"));
            response.put("domainId", rs.getInt("domain_id"));
            response.put("domainName", rs.getString("domain_name"));
            response.put("offerDate", rs.getString("created_at"));
            response.put("quotePrice", rs.getBigDecimal("quote_price"));
            response.put("isAccepted", rs.getBoolean("is_accepted"));
            return response;
        });

        logger.debug("Raw output from database: {}", rows);

        List<Map<String, Object>> finalOutput = mapData(rows);

        logger.debug("Transformed output: {}", finalOutput);

        return finalOutput;
    }

    private List<Map<String, Object>> mapData(List<Map<String, Object>> rows) {
        logger.info("Mapping data to master agreements with role offers");

        Map<Integer, Map<String, Object>> masterAgreementMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            int masterAgreementTypeId = (int) row.get("masterAgreementTypeId");

            Map<String, Object> masterAgreement = masterAgreementMap.computeIfAbsent(masterAgreementTypeId, id -> {
                Map<String, Object> agreement = new LinkedHashMap<>();
                agreement.put("masterAgreementTypeId", row.get("masterAgreementTypeId"));
                agreement.put("masterAgreementTypeName", row.get("masterAgreementTypeName"));
                agreement.put("validFrom", formatDateForSQL((Date) row.get("validFrom")));
                agreement.put("validUntil", formatDateForSQL((Date) row.get("validUntil")));
                agreement.put("status", "open");
                agreement.put("createdAt", formatDateForSQL(new Date(System.currentTimeMillis())));
                agreement.put("domains", new ArrayList<Map<String, Object>>());
                return agreement;
            });

            List<Map<String, Object>> domains = (List<Map<String, Object>>) masterAgreement.get("domains");

            int domainId = (int) row.get("domainId");
            Map<String, Object> domain = domains.stream()
                    .filter(d -> (int) d.get("domainId") == domainId)
                    .findFirst()
                    .orElseGet(() -> {
                        Map<String, Object> domainMap = new LinkedHashMap<>();
                        domainMap.put("domainId", domainId);
                        domainMap.put("domainName", row.get("domainName"));
                        domainMap.put("roleOffer", new ArrayList<Map<String, Object>>());
                        domains.add(domainMap);
                        return domainMap;
                    });

            List<Map<String, Object>> roleOffers = (List<Map<String, Object>>) domain.get("roleOffer");
            Map<String, Object> roleOffer = new LinkedHashMap<>();
            roleOffer.put("roleId", row.get("roleId"));
            roleOffer.put("roleName", row.get("roleName"));
            roleOffer.put("experienceLevel", row.get("experienceLevel"));
            roleOffer.put("technologiesCatalog", row.get("technologiesCatalog"));
            roleOffer.put("quotePrice", row.get("quotePrice"));
            roleOffer.put("offerDate", row.get("createdAt"));

            roleOffers.add(roleOffer);
        }

        return new ArrayList<>(masterAgreementMap.values());
    }

    @Transactional
    public CommonResponse createMasterAgreementOffer(MasterAgreementRequest request) {
        try {
            logger.info("Creating Master Agreement offer with ID: {}", request.getMasterAgreementTypeId());

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

            String offerSql = "INSERT INTO offer " +
                    "(domain_id, domain_name, role_name, experience_level, technologies_catalog, quote_price, offer_date, master_agreement_type_name, status, master_agreement_type_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            for (Domain domain : request.getDomains()) {
                for (RoleOffer roleOffer : domain.getRoleOffer()) {
                    jdbcTemplate.update(offerSql,
                            domain.getDomainId(),
                            domain.getDomainName(),
                            roleOffer.getRoleName(),
                            roleOffer.getExperienceLevel(),
                            roleOffer.getTechnologiesCatalog(),
                            roleOffer.getQuotePrice(),
                            request.getCreatedAt(),
                            request.getMasterAgreementTypeName(),
                            request.getStatus(),
                            request.getMasterAgreementTypeId()
                    );
                }
            }

            logger.info("Master Agreement successfully created with ID: {}", request.getMasterAgreementTypeId());
            return new CommonResponse(true, "Master Agreement successfully created", null);

        } catch (Exception e) {
            logger.error("Error while creating Master Agreement: {}", e.getMessage(), e);
            return new CommonResponse(false, "Error creating Master Agreement: " + e.getMessage(), null);
        }
    }


    @Transactional
    public boolean updateOffer(RoleOffer request) {
        try {
            logger.info("Updating offer with role ID: {}", request.getRoleId());

            String selectSql = "SELECT role_id, domain_id, domain_name, role_name, experience_level, technologies_catalog, quote_price, offer_date, master_agreement_type_name, status " +
                    "FROM offer " +
                    "WHERE role_id = ? AND domain_id = ? AND domain_name = ? AND role_name = ? AND master_agreement_type_name = ? AND experience_level = ?";

            logger.debug("Executing SQL query to fetch offer: {}", selectSql);
            List<Map<String, Object>> offers = jdbcTemplate.queryForList(selectSql,
                    request.getRoleId(),
                    request.getDomainId(),
                    request.getDomainName(),
                    request.getRoleName(),
                    request.getMasterAgreementTypeName(),
                    request.getExperienceLevel()
            );

            if (offers.isEmpty()) {
                logger.warn("No offer found to update with role ID: {}", request.getRoleId());
                return false;
            }

            String insertSql = "INSERT INTO role_offer (id, role_id, role_name, experience_level, technologies_catalog, domain_name, domain_id, quote_price, master_agreement_type_id, " +
                    " master_agreement_type_name, provider, bid_price, provider_id ) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            for (Map<String, Object> offer : offers) {
                jdbcTemplate.update(insertSql,
                        request.getId(),
                        offer.get("role_id"),
                        offer.get("role_name"),
                        offer.get("experience_level"),
                        offer.get("technologies_catalog"),
                        offer.get("domain_name"),
                        offer.get("domain_id"),
                        offer.get("quote_price"),
                        request.getMasterAgreementTypeId(),
                        request.getMasterAgreementTypeName(),
                        request.getProvider(),
                        request.getBidPrice(),
                        request.getProviderId()
                );
            }

            logger.info("Offer successfully updated for role ID: {}", request.getRoleId());
            return true;
        } catch (Exception e) {
            logger.error("Error while updating offer: {}", e.getMessage(), e);
            throw new RuntimeException("Error while updating offer: " + e.getMessage(), e);
        }
    }

    private String formatDateForSQL(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
