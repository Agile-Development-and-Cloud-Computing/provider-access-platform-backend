package com.fuas.providers_access_platform.dto;

import java.math.BigDecimal;
import java.util.Date;

public class MasterAgreementResponse {

    private int masterAgreementTypeId;
    private String masterAgreementTypeName;
    private Date validFrom;
    private Date validUntil;
    private String roleName;
    private String experienceLevel;
    private String technologiesCatalog;
    private int domainId;
    private String domainName;
    private String offerCycle;
    private String provider;
    private BigDecimal quotePrice;
    private boolean isAccepted;

    public int getMasterAgreementTypeId() {
        return masterAgreementTypeId;
    }

    public void setMasterAgreementTypeId(int masterAgreementTypeId) {
        this.masterAgreementTypeId = masterAgreementTypeId;
    }

    public String getMasterAgreementTypeName() {
        return masterAgreementTypeName;
    }

    public void setMasterAgreementTypeName(String masterAgreementTypeName) {
        this.masterAgreementTypeName = masterAgreementTypeName;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
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

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
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

    public BigDecimal getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        this.quotePrice = quotePrice;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
