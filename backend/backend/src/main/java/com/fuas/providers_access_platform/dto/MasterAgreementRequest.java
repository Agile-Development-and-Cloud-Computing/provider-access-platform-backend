package com.fuas.providers_access_platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fuas.providers_access_platform.model.Domain;

import java.util.ArrayList;
import java.util.List;

public class MasterAgreementRequest {

    @JsonProperty("agreementId")
    private Integer masterAgreementTypeId;
    @JsonProperty("name")
    private String masterAgreementTypeName;
    private String validFrom;
    private String validUntil;
    private String status;
    private String createdAt;
    @JsonProperty("agreementDetails")
    private List<Domain> domains = new ArrayList<>();

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

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }


    @Override
    public String toString() {
        return "MasterAgreementRequest{" +
                ", masterAgreementTypeId=" + masterAgreementTypeId +
                ", masterAgreementTypeName='" + masterAgreementTypeName + '\'' +
                ", validFrom='" + validFrom + '\'' +
                ", validUntil='" + validUntil + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", domains=" + domains +
                '}';
    }
}
