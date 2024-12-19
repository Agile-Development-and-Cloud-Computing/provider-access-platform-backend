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
import java.util.stream.Collectors;

@Service
public class RequestManagementService {

    @Autowired
    private RoleOfferRepository roleOfferRepository;


    public List<AgreementOfferResponse> getAllOffersGrouped() {
        // Fetch all role offers from the database
        List<RoleOffer> roleOffers = roleOfferRepository.findAll();

        // Group by roleName, masterAgreementTypeName, and domainId
        Map<String, List<RoleOffer>> groupedOffers = roleOffers.stream().collect(Collectors.groupingBy(
                offer -> offer.getRoleName() + "|" + offer.getMasterAgreementTypeName() + "|" + offer.getDomainId()
        ));

        // Transform the grouped data into RoleOfferResponseDTO
        List<AgreementOfferResponse> responseList = new ArrayList<>();

        for (Map.Entry<String, List<RoleOffer>> entry : groupedOffers.entrySet()) {
            List<RoleOffer> offers = entry.getValue();

            RoleOffer firstOffer = offers.get(0);
            AgreementOfferResponse response = new AgreementOfferResponse();
            response.setRoleName(firstOffer.getRoleName());
            response.setExperienceLevel(firstOffer.getExperienceLevel());
            response.setTechnologiesCatalog(firstOffer.getTechnologiesCatalog());
            response.setDomainId(firstOffer.getDomainId());
            response.setDomainName(firstOffer.getDomainName());
            response.setMasterAgreementTypeId(firstOffer.getMasterAgreementTypeId());
            response.setMasterAgreementTypeName(firstOffer.getMasterAgreementTypeName());

            // Map providers
            List<ProviderResponse> providers = offers.stream().map(offer -> {
                ProviderResponse provider = new ProviderResponse();
                provider.setOfferId(offer.getId());
                provider.setName(offer.getProvider());
                provider.setQuotePrice(offer.getQuotePrice());
                provider.setIsAccepted(offer.getIsAccepted());
                provider.setCycle(offer.getOfferCycle());
                return provider;
            }).collect(Collectors.toList());

            response.setProvider(providers);
            responseList.add(response);
        }

        return responseList;
    }

    public void updateOfferResponse(Long offerId, Boolean isAccepted) {
        // Fetch the record by offerId
        RoleOffer offer = roleOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer with ID " + offerId + " not found."));

        // Update the isAccepted field
        offer.setIsAccepted(isAccepted);

        // Save the updated offer back to the database
        roleOfferRepository.save(offer);
    }
}
