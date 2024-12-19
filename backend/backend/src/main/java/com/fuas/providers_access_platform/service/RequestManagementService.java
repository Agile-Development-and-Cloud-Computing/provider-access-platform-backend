package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.AgreementOfferResponse;
import com.fuas.providers_access_platform.dto.ProviderResponse;
import com.fuas.providers_access_platform.model.RoleOffer;
import com.fuas.providers_access_platform.repository.RoleOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestManagementService {

    @Autowired
    private RoleOfferRepository roleOfferRepository;

    public List<AgreementOfferResponse> getAgreementOffers() {
        // Fetch all role offers from the database
        List<RoleOffer> roleOffers = roleOfferRepository.findAll();

        // Group and aggregate the data
        Map<String, AgreementOfferResponse> aggregatedData = new HashMap<>();

        for (RoleOffer offer : roleOffers) {
            String key = offer.getRoleName() + "-" + offer.getMasterAgreementTypeName() + "-" + offer.getDomainId();

            // If the group does not exist, create a new entry
            aggregatedData.putIfAbsent(key, new AgreementOfferResponse(
                    offer.getRoleName(),
                    offer.getExperienceLevel(),
                    offer.getTechnologiesCatalog(),
                    offer.getDomainId(),
                    offer.getDomainName(),
                    offer.getMasterAgreementTypeId(),
                    offer.getMasterAgreementTypeName(),
                    new ArrayList<>()
            ));

            // Add provider information to the existing group
            AgreementOfferResponse response = aggregatedData.get(key);
            response.getProvider().add(new ProviderResponse(
                    offer.getId(),
                    offer.getProvider(),
                    offer.getQuotePrice(),
                    offer.getIsAccepted(),
                    offer.getOfferCycle()
            ));
        }

        // Convert the aggregated data to a list and return
        return new ArrayList<>(aggregatedData.values());
    }
}
