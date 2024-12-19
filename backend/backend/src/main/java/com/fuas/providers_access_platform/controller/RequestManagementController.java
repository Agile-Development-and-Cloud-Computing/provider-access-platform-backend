package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.AgreementOfferResponse;
import com.fuas.providers_access_platform.service.RequestManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RequestManagementController {


    @Autowired
    private RequestManagementService requestManagementService;

    @GetMapping("/agreement-offers")
    public List<AgreementOfferResponse> getAgreementOffers() {
        return requestManagementService.getAgreementOffers();
    }

}
