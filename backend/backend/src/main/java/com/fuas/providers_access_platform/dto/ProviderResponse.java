package com.fuas.providers_access_platform.dto;


public class ProviderResponse {
    private Long offerId;
    private String name;
    private Double quotePrice;
    private Boolean isAccepted;
    private String cycle;

    // Constructor, Getters, and Setters

    public ProviderResponse(Long offerId, String name, Double quotePrice, Boolean isAccepted, String cycle) {
        this.offerId = offerId;
        this.name = name;
        this.quotePrice = quotePrice;
        this.isAccepted = isAccepted;
        this.cycle = cycle;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
// Getters and setters omitted for brevity
}
