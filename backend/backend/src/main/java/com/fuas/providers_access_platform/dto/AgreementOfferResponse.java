package com.fuas.providers_access_platform.dto;

import java.util.List;

public class AgreementOfferResponse {

    private String roleName;
    private String experienceLevel;
    private String technologiesCatalog;
    private Integer domainId;
    private String domainName;
    private Integer masterAgreementTypeId;
    private String masterAgreementTypeName;
    private List<ProviderResponse> provider;

    // Constructor, Getters, and Setters

    public AgreementOfferResponse(String roleName, String experienceLevel, String technologiesCatalog, Integer domainId, String domainName, Integer masterAgreementTypeId, String masterAgreementTypeName, List<ProviderResponse> provider) {
        this.roleName = roleName;
        this.experienceLevel = experienceLevel;
        this.technologiesCatalog = technologiesCatalog;
        this.domainId = domainId;
        this.domainName = domainName;
        this.masterAgreementTypeId = masterAgreementTypeId;
        this.masterAgreementTypeName = masterAgreementTypeName;
        this.provider = provider;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getTechnologiesCatalog() {
        return technologiesCatalog;
    }

    public void setTechnologiesCatalog(String technologiesCatalog) {
        this.technologiesCatalog = technologiesCatalog;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getMasterAgreementTypeId() {
        return masterAgreementTypeId;
    }

    public void setMasterAgreementTypeId(Integer masterAgreementTypeId) {
        this.masterAgreementTypeId = masterAgreementTypeId;
    }

    public String getMasterAgreementTypeName() {
        return masterAgreementTypeName;
    }

    public void setMasterAgreementTypeName(String masterAgreementTypeName) {
        this.masterAgreementTypeName = masterAgreementTypeName;
    }

    public List<ProviderResponse> getProvider() {
        return provider;
    }

    public void setProvider(List<ProviderResponse> provider) {
        this.provider = provider;
    }
}
