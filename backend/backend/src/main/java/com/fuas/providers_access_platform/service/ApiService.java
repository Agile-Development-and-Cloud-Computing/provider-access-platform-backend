package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ApiService {

    private final WebClient webClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    String validFromFormatted ;
    String validUntilFormatted ;
    String createdAtFormatted;

    @Autowired
    public ApiService(WebClient.Builder webClientBuilder, JdbcTemplate jdbcTemplate) {
        this.webClient = webClientBuilder.baseUrl("https://agiledev-contractandprovidermana-production.up.railway.app/master-agreements").build();
        this.jdbcTemplate = jdbcTemplate;
    }


    public void fetchAndInsertAgreements() {
        // Fetch agreements from external API
        List<MasterAgreementRequest> agreements = webClient.get()
                .uri("/all-open-agreements")
                .retrieve()
                .bodyToFlux(MasterAgreementRequest.class)
                .collectList()
                .block();


        if (agreements != null && !agreements.isEmpty()) {

            System.out.println("JSON Response from External API: ");
            agreements.forEach(agreement -> {
                System.out.println(agreement);
            });
            // Insert each agreement into the database
            agreements.forEach(agreement -> {
                validFromFormatted = formatDateForSQL(agreement.getValidFrom());
                validUntilFormatted = formatDateForSQL(agreement.getValidUntil());
                createdAtFormatted = formatDateForSQL(agreement.getCreatedAt());

                String masterAgreementSql = "INSERT INTO master_agreement_types (master_agreement_type_id, master_agreement_type_name, valid_from, valid_until, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(masterAgreementSql,
                        agreement.getMasterAgreementTypeId(),
                        agreement.getMasterAgreementTypeName(),
                        validFromFormatted,
                        validUntilFormatted,
                        agreement.getStatus(),
                        createdAtFormatted);

                agreement.getDomains().forEach(domain -> {
                    domain.getRoleOffer().forEach(roleOffer -> {
                        String offerSql = "INSERT INTO offer (role_id, domain_id, domain_name, role_name, experience_level, technologies_catalog, quote_price, offer_date, master_agreement_type_name, status, master_agreement_type_id) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        jdbcTemplate.update(offerSql,
                                roleOffer.getRoleId(),
                                domain.getDomainId(),
                                domain.getDomainName(),
                                roleOffer.getRoleName(),
                                roleOffer.getExperienceLevel(),
                                roleOffer.getTechnologiesCatalog(),
                                roleOffer.getQuotePrice(),
                                createdAtFormatted,
                                agreement.getMasterAgreementTypeName(),
                                agreement.getStatus(),
                                agreement.getMasterAgreementTypeId());
                    });
                });
            });
                System.out.println("Agreements successfully inserted into the database.");
        } else {
            System.out.println("No agreements found in the external API response.");
        }
    }

    private String formatDateForSQL(String date) {
        // Parse the ISO 8601 string (e.g., '2024-01-01T00:00:00.000Z') into LocalDateTime
        Instant instant = Instant.parse(date);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);

        // Format to SQL-compatible format (yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

}
