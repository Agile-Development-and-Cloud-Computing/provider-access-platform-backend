package com.fuas.providers_access_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class ServiceRequest {

    @JsonProperty("offerId")
    private Long id;
    private String requestID;
    private int masterAgreementID;
    private String masterAgreementName;
    private String taskDescription;
    private String requestType;
    private String project;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cycleStatus;
    private String selectedDomainName;
    private int numberOfSpecialists;
    private int numberOfOffers;
    private String isApproved;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createdBy;

    private String comments;
    private List<ServiceOffer> serviceOffers;

    public static class ServiceOffer {
        private String providerID;
        private String providerName;
        private String employeeID;
        private String role;
        private String level;
        private String technologyLevel;
        private String locationType;
        private Integer domainId;
        private String domainName;
        private String userId;

        // Getters and Setters

        public String getProviderID() {
            return providerID;
        }

        public void setProviderID(String providerID) {
            this.providerID = providerID;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getEmployeeID() {
            return employeeID;
        }

        public void setEmployeeID(String employeeID) {
            this.employeeID = employeeID;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getTechnologyLevel() {
            return technologyLevel;
        }

        public void setTechnologyLevel(String technologyLevel) {
            this.technologyLevel = technologyLevel;
        }

        public String getLocationType() {
            return locationType;
        }

        public void setLocationType(String locationType) {
            this.locationType = locationType;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "ServiceOffer{" +
                    "providerID='" + providerID + '\'' +
                    ", providerName='" + providerName + '\'' +
                    ", employeeID='" + employeeID + '\'' +
                    ", role='" + role + '\'' +
                    ", level='" + level + '\'' +
                    ", technologyLevel='" + technologyLevel + '\'' +
                    ", locationType='" + locationType + '\'' +
                    ", domainId=" + domainId +
                    ", domainName='" + domainName + '\'' +
                    ", userId='" + userId + '\'' +
                    '}';
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public int getMasterAgreementID() {
        return masterAgreementID;
    }

    public void setMasterAgreementID(int masterAgreementID) {
        this.masterAgreementID = masterAgreementID;
    }

    public String getMasterAgreementName() {
        return masterAgreementName;
    }

    public void setMasterAgreementName(String masterAgreementName) {
        this.masterAgreementName = masterAgreementName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCycleStatus() {
        return cycleStatus;
    }

    public void setCycleStatus(String cycleStatus) {
        this.cycleStatus = cycleStatus;
    }

    public String getSelectedDomainName() {
        return selectedDomainName;
    }

    public void setSelectedDomainName(String selectedDomainName) {
        this.selectedDomainName = selectedDomainName;
    }

    public int getNumberOfSpecialists() {
        return numberOfSpecialists;
    }

    public void setNumberOfSpecialists(int numberOfSpecialists) {
        this.numberOfSpecialists = numberOfSpecialists;
    }

    public int getNumberOfOffers() {
        return numberOfOffers;
    }

    public void setNumberOfOffers(int numberOfOffers) {
        this.numberOfOffers = numberOfOffers;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public List<ServiceOffer> getServiceOffers() {
        return serviceOffers;
    }

    public void setServiceOffers(List<ServiceOffer> serviceOffers) {
        this.serviceOffers = serviceOffers;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "id=" + id +
                ", requestID='" + requestID + '\'' +
                ", masterAgreementID=" + masterAgreementID +
                ", masterAgreementName='" + masterAgreementName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", requestType='" + requestType + '\'' +
                ", project='" + project + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", cycleStatus='" + cycleStatus + '\'' +
                ", selectedDomainName='" + selectedDomainName + '\'' +
                ", numberOfSpecialists=" + numberOfSpecialists +
                ", numberOfOffers=" + numberOfOffers +
                ", isApproved='" + isApproved + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", comments='" + comments + '\'' +
                ", serviceOffers=" + serviceOffers +
                '}';
    }
}


