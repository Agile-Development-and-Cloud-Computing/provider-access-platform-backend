package com.fuas.providers_access_platform.service;
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


}
