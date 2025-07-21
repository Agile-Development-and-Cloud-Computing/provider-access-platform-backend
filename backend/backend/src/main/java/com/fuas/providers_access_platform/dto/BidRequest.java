package com.fuas.providers_access_platform.dto;

public class BidRequest {

    private int serviceId;
    private int providerId;
    private double bidAmount;
    private Integer employeeId;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public String toString() {
        return "BidRequest{" +
                "serviceId=" + serviceId +
                ", providerId=" + providerId +
                ", bidAmount=" + bidAmount +
                ", employeeId=" + employeeId +
                '}';
    }
}
