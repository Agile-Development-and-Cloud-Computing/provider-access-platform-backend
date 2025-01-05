package com.fuas.providers_access_platform.model;


import jakarta.persistence.*;

@Entity
@Table(name = "role_offer")
public class RoleOffer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName;
    private String experienceLevel;
    private String technologiesCatalog;
    private Integer domainId;
    private String domainName;
    private Integer masterAgreementTypeId;
    private String masterAgreementTypeName;
    private String provider;
    private Double quotePrice;
    private Boolean isAccepted;
    private String offerCycle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public String getOfferCycle() {
        return offerCycle;
    }

    public void setOfferCycle(String offerCycle) {
        this.offerCycle = offerCycle;
    }

    @Override
    public String toString() {
        return "RoleOffer{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", technologiesCatalog='" + technologiesCatalog + '\'' +
                ", domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                ", masterAgreementTypeId=" + masterAgreementTypeId +
                ", masterAgreementTypeName='" + masterAgreementTypeName + '\'' +
                ", provider='" + provider + '\'' +
                ", quotePrice=" + quotePrice +
                ", isAccepted=" + isAccepted +
                ", offerCycle='" + offerCycle + '\'' +
                '}';
    }
}
