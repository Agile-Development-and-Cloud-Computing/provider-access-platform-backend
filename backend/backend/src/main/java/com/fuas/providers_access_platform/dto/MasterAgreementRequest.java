package com.fuas.providers_access_platform.dto;

public class MasterAgreementRequest {

    private Integer masterAgreementTypeId;
    private String masterAgreementTypeName;
    private String roleName;
    private String experienceLevel;
    private String technologiesCatalog;
    private Integer domainId;
    private String domainName;
    private String offerCycle;
    private String provider;
    private Double quotePrice;

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

    public String getOfferCycle() {
        return offerCycle;
    }

    public void setOfferCycle(String offerCycle) {
        this.offerCycle = offerCycle;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    @Override
    public String toString() {
        return "MasterAgreementRequest{" +
                "masterAgreementTypeId=" + masterAgreementTypeId +
                ", masterAgreementTypeName='" + masterAgreementTypeName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", technologiesCatalog='" + technologiesCatalog + '\'' +
                ", domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                ", offerCycle='" + offerCycle + '\'' +
                ", provider='" + provider + '\'' +
                ", quotePrice=" + quotePrice +
                '}';
    }
}
