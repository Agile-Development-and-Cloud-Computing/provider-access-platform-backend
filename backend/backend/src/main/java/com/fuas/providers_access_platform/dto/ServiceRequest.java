package com.fuas.providers_access_platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Member;
import java.util.List;

public class ServiceRequest {

    @JsonProperty("ServiceRequestId")
    private String serviceRequestId;
    private int agreementId;
    private String agreementName;
    private String taskDescription;
    private String project;
    private String begin;
    private String end;
    private int amountOfManDays;
    private String location;
    private String type;
    private String cycleStatus;
    private int numberOfSpecialists;
    private int numberOfOffers;
    private String consumer;
    private String locationType;
    private String informationForProviderManager;
    private List<SelectedMember> selectedMembers;
    private List<String> notifications;


    public static class SelectedMember {
        private int domainId;
        private String domainName;
        private String role;
        private String level;
        private String technologyLevel;
        @JsonProperty("_id")
        private String id;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "SelectedMember{" +
                    "domainId=" + domainId +
                    ", domainName='" + domainName + '\'' +
                    ", role='" + role + '\'' +
                    ", level='" + level + '\'' +
                    ", technologyLevel='" + technologyLevel + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public int getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }

    public String getAgreementName() {
        return agreementName;
    }

    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getAmountOfManDays() {
        return amountOfManDays;
    }

    public void setAmountOfManDays(int amountOfManDays) {
        this.amountOfManDays = amountOfManDays;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCycleStatus() {
        return cycleStatus;
    }

    public void setCycleStatus(String cycleStatus) {
        this.cycleStatus = cycleStatus;
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

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getInformationForProviderManager() {
        return informationForProviderManager;
    }

    public void setInformationForProviderManager(String informationForProviderManager) {
        this.informationForProviderManager = informationForProviderManager;
    }

    public List<SelectedMember> getSelectedMembers() {
        return selectedMembers;
    }

    public void setSelectedMembers(List<SelectedMember> selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }



    @Override
    public String toString() {
        return "ServiceRequest{" +
                "serviceRequestId='" + serviceRequestId + '\'' +
                ", agreementId=" + agreementId +
                ", agreementName='" + agreementName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", project='" + project + '\'' +
                ", begin='" + begin + '\'' +
                ", end='" + end + '\'' +
                ", amountOfManDays=" + amountOfManDays +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", cycleStatus='" + cycleStatus + '\'' +
                ", numberOfSpecialists=" + numberOfSpecialists +
                ", numberOfOffers=" + numberOfOffers +
                ", consumer='" + consumer + '\'' +
                ", locationType='" + locationType + '\'' +
                ", informationForProviderManager='" + informationForProviderManager + '\'' +
                ", selectedMembers=" + selectedMembers +
                ", notifications=" + notifications +
                '}';
    }
}


