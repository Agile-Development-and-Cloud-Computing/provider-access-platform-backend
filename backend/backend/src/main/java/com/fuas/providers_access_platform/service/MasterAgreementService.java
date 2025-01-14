package com.fuas.providers_access_platform.service;
import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.model.Domain;
import com.fuas.providers_access_platform.model.RoleOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MasterAgreementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

        public List<Map<String, Object>> getMasterAgreementsWithRoleOffer() {
            // SQL query to fetch master agreements and role offers
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
                    " WHERE ro.is_accepted = 0;"; // Example condition to fetch only accepted offers

            System.out.println("Select Query --->" + sql);

            // Execute the query and process the result set
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

            // Log the raw output from the database
            System.out.println("Raw Output from Database: ");
            rows.forEach(row -> System.out.println(row));

            // Transforming the data into the required format
            List<Map<String, Object>> finalOutput = mapData(rows);

            // Log the final output
            System.out.println("Transformed Output: ");
            finalOutput.forEach(output -> System.out.println(output));

            return finalOutput;
        }

    private List<Map<String, Object>> mapData(List<Map<String, Object>> rows) {
        // Group the rows by masterAgreementTypeId
        Map<Integer, Map<String, Object>> masterAgreementMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            int masterAgreementTypeId = (int) row.get("masterAgreementTypeId");

            // Fetch or create the agreement structure
            Map<String, Object> masterAgreement = masterAgreementMap.computeIfAbsent(masterAgreementTypeId, id -> {
                Map<String, Object> agreement = new LinkedHashMap<>();
                agreement.put("masterAgreementTypeId", row.get("masterAgreementTypeId"));
                agreement.put("masterAgreementTypeName", row.get("masterAgreementTypeName"));
                agreement.put("validFrom", formatDateForSQL((Date) row.get("validFrom")));
                agreement.put("validUntil", formatDateForSQL((Date) row.get("validUntil")));
                agreement.put("status", "open"); // Assuming a default value for status
                agreement.put("createdAt", formatDateForSQL(new Date(System.currentTimeMillis()))); // Default createdAt as current time
                agreement.put("domains", new ArrayList<Map<String, Object>>());
                return agreement;
            });

            // Fetch the list of domains
            List<Map<String, Object>> domains = (List<Map<String, Object>>) masterAgreement.get("domains");

            // Check if the domain already exists in the list
            int domainId = (int) row.get("domainId");
            Map<String, Object> domain = domains.stream()
                    .filter(d -> (int) d.get("domainId") == domainId)
                    .findFirst()
                    .orElseGet(() -> {
                        // Create a new domain if it doesn't exist
                        Map<String, Object> domainMap = new LinkedHashMap<>();
                        domainMap.put("domainId", domainId);
                        domainMap.put("domainName", row.get("domainName"));
                        domainMap.put("roleOffer", new ArrayList<Map<String, Object>>());
                        domains.add(domainMap);
                        return domainMap;
                    });

            // Add the role offer to the domain
            List<Map<String, Object>> roleOffers = (List<Map<String, Object>>) domain.get("roleOffer");
            Map<String, Object> roleOffer = new LinkedHashMap<>();
            roleOffer.put("roleId",row.get("roleId"));
            roleOffer.put("roleName", row.get("roleName"));
            roleOffer.put("experienceLevel", row.get("experienceLevel"));
            roleOffer.put("technologiesCatalog", row.get("technologiesCatalog"));
            roleOffer.put("quotePrice", row.get("quotePrice"));
            roleOffer.put("offerDate", row.get("createdAt")); // Assuming offer date is createdAt

            roleOffers.add(roleOffer);
        }

        // Convert the final map into a list to return
        return new ArrayList<>(masterAgreementMap.values());
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
                    "technologies_catalog, quote_price, offer_date, master_agreement_type_name, status, master_agreement_type_id) " +    //quote price comes from Group 1
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                            request.getStatus(),
                            request.getMasterAgreementTypeId()
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
                    "Role Id: " + request.getRoleId() + ", " +
                    "Role Name: " + request.getRoleName() + ", " +
                    "Master Agreement Type Name: " + request.getMasterAgreementTypeName() + ", " +
                    "Master Agreement Type ID: " + request.getMasterAgreementTypeId() +"," +
                    "Experience Level :" + request.getExperienceLevel());


            // SQL query to select data from the 'offer' table based on the provided parameters
            String selectSql = "SELECT role_id, domain_id, domain_name, role_name, experience_level, technologies_catalog, quote_price, offer_date, master_agreement_type_name, status " +
                    "FROM offer " +
                    "WHERE role_id = ? " +
                    "AND domain_id = ? " +
                    "AND domain_name = ? " +
                    "AND role_name = ? " +
                    "AND master_agreement_type_name = ?" +
                    "AND experience_level = ? ";

            // Fetch the data from the 'offer' table based on the request
            List<Map<String, Object>> offers = jdbcTemplate.queryForList(selectSql,
                    request.getRoleId(),
                    request.getDomainId(),
                    request.getDomainName(),
                    request.getRoleName(),
                    request.getMasterAgreementTypeName(),
                    request.getExperienceLevel()
            );

            // Check if the query result is empty
            if (offers.isEmpty()) {
                return false;
            }

            // SQL query to insert into role_offers
            String insertSql = "INSERT INTO role_offer (id, role_id, role_name, experience_level, " +
                    "technologies_catalog, domain_name, domain_id, quote_price, master_agreement_type_id, " +
                    " master_agreement_type_name, provider, bid_price ) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Loop through the offers retrieved from the 'offer' table
            for (Map<String, Object> offer : offers) {
                jdbcTemplate.update(insertSql,
                        request.getId(),
                        offer.get("role_id"),
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


    private String formatDateForSQL(Date date) {
        // Format the date to "yyyy-MM-dd" format for SQL-compatible dates
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
