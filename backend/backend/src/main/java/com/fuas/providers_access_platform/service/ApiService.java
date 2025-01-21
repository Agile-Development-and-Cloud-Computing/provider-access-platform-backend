package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.MasterAgreementRequest;
import com.fuas.providers_access_platform.dto.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private final WebClient masterAgreementWebClient; // For Master Agreement API
    private final WebClient serviceRequestWebClient;  // For Service Request API

    @Autowired
    private JdbcTemplate jdbcTemplate;


    String validFromFormatted ;
    String validUntilFormatted ;
    String createdAtFormatted;

    @Autowired
    public ApiService(WebClient.Builder webClientBuilder, JdbcTemplate jdbcTemplate) {
        this.masterAgreementWebClient = webClientBuilder
                .baseUrl("https://agiledev-contractandprovidermana-production.up.railway.app/master-agreements")
                .build();
        this.serviceRequestWebClient = webClientBuilder
                .baseUrl("https://service-management-backend-production.up.railway.app/api/service-requests")
                .build();
        this.jdbcTemplate = jdbcTemplate;
    }

    // Scheduled task runs every 15 minutes
    @Scheduled(fixedRate = 30000)  // 120000 ms = 2 minutes
    public void fetchAndInsertAgreements() {

        logger.info("Starting scheduled task: fetchAndInsertAgreements at {}", LocalDateTime.now());

        // Fetch agreements from external API
        List<MasterAgreementRequest> agreements = masterAgreementWebClient.get()
                .uri("/all-open-agreements")
                .retrieve()
                .bodyToFlux(MasterAgreementRequest.class)
                .collectList()
                .block();


        if (agreements != null && !agreements.isEmpty()) {
            logger.info("Fetched {} agreements from the external API.", agreements.size());

            agreements.forEach(agreement -> {
                    if (!isAgreementExists(agreement.getMasterAgreementTypeId())) {
                        validFromFormatted = formatDateForSQL(agreement.getValidFrom());
                        validUntilFormatted = formatDateForSQL(agreement.getValidUntil());
                        createdAtFormatted = formatDateForSQL(agreement.getCreatedAt());

                        logger.debug("Inserting agreement: {}", agreement);

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

                                logger.debug("Inserting role offer for agreement ID {}: {}",agreement.getMasterAgreementTypeId(), roleOffer);

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
                        logger.info("Agreement with ID {} successfully inserted into the database.", agreement.getMasterAgreementTypeId());                    }
                });
        } else {
            logger.warn("No agreements found in the external API response.");
        }
    }

    // Scheduled task for Service Requests
    @Scheduled(fixedRate = 30000)  // 120000 2 minutes interval
    public void fetchAndInsertServiceRequests() {
        logger.info("Starting scheduled task: fetchAndInsertServiceRequests at {}", LocalDateTime.now());

        // Fetch service requests from the external API
        List<ServiceRequest> serviceRequests = serviceRequestWebClient.get()
                .uri("/published")
                .retrieve()
                .bodyToFlux(ServiceRequest.class)
                .collectList()
                .block();


        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            logger.info("Fetched {} service requests from the external API.", serviceRequests.size());

            // Insert each service request into the database
            serviceRequests.forEach(serviceRequest -> {

                if (!isServiceRequestExists(serviceRequest.getServiceRequestId())) {
                    logger.debug("Inserting service request: {}", serviceRequest);

                    String beginDateFormatted = formatDateForSQL(serviceRequest.getBegin());
                    String endDateFormatted = formatDateForSQL(serviceRequest.getEnd());
                    // Join notifications into a single string
                    String notifications = String.join("; ", serviceRequest.getNotifications());

                    String serviceRequestSql = "INSERT INTO service_agreement_types (service_request_id, agreement_id, agreement_name, task_description, project, begin_date, end_date, " +
                            "amount_of_man_days, location, type, cycle_status, number_of_specialists, number_of_offers, consumer, location_type, information_for_provider_manager, notifications) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(serviceRequestSql,
                            serviceRequest.getServiceRequestId(),
                            serviceRequest.getAgreementId(),
                            serviceRequest.getAgreementName(),
                            serviceRequest.getTaskDescription(),
                            serviceRequest.getProject(),
                            beginDateFormatted,
                            endDateFormatted,
                            serviceRequest.getAmountOfManDays(),
                            serviceRequest.getLocation(),
                            serviceRequest.getType(),
                            serviceRequest.getCycleStatus(),
                            serviceRequest.getNumberOfSpecialists(),
                            serviceRequest.getNumberOfOffers(),
                            serviceRequest.getConsumer(),
                            serviceRequest.getLocationType(),
                            serviceRequest.getInformationForProviderManager(),
                            notifications);

                    serviceRequest.getSelectedMembers().forEach(member -> {
                        logger.debug("Inserting selected member: {}", member);

                        String memberSql = "INSERT INTO service_request (service_request_id, domain_id, domain_name, role, level, technology_level, member_id) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        jdbcTemplate.update(memberSql,
                                serviceRequest.getServiceRequestId(),
                                member.getDomainId(),
                                member.getDomainName(),
                                member.getRole(),
                                member.getLevel(),
                                member.getTechnologyLevel(),
                                member.getId());
                    });
                    logger.info("Service Request with ID {} successfully inserted into the database.", serviceRequest.getServiceRequestId());
                }
            });
        } else {
            logger.warn("No service requests found in the external API response.");
        }
    }

    private boolean isAgreementExists(int agreementId) {
        String sql = "SELECT COUNT(*) FROM master_agreement_types WHERE master_agreement_type_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, agreementId);
        return count != null && count > 0;
    }

    // Check if the service request already exists
    private boolean isServiceRequestExists(String serviceRequestId) {
        String sql = "SELECT COUNT(*) FROM service_agreement_types WHERE service_request_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceRequestId);
        return count != null && count > 0;
    }


    private String formatDateForSQL(String date) {
        if (date == null || date.isEmpty()) {
            return null; // Or return a default date string if needed
        }
        try {
            Instant instant = Instant.parse(date); // Ensure correct format here
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();  // Log or handle parsing errors
            return null; // Or a default value
        }
    }

}
