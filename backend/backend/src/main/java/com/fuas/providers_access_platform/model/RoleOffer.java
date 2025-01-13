package com.fuas.providers_access_platform.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "role_offer")
public class RoleOffer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("role")
    private String roleName;
    @JsonProperty("level")
    private String experienceLevel;
    @JsonProperty("technologyLevel")
    private String technologiesCatalog;
    private String domainName;
    private String domainId;
    private Integer masterAgreementTypeId;
    private String masterAgreementTypeName;
    private String provider;
    @JsonProperty("price")
    private Double quotePrice;
    @JsonProperty("cycle")
    private String offerCycle;
    private Double bidPrice;


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

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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

    public String getOfferCycle() {
        return offerCycle;
    }

    public void setOfferCycle(String offerCycle) {
        this.offerCycle = offerCycle;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    @Override
    public String toString() {
        return "RoleOffer{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", technologiesCatalog='" + technologiesCatalog + '\'' +
                ", domainName='" + domainName + '\'' +
                ", domainId='" + domainId + '\'' +
                ", masterAgreementTypeId=" + masterAgreementTypeId +
                ", masterAgreementTypeName='" + masterAgreementTypeName + '\'' +
                ", provider='" + provider + '\'' +
                ", quotePrice=" + quotePrice +
                ", offerCycle='" + offerCycle + '\'' +
                ", bidPrice=" + bidPrice +
                '}';
    }
}
