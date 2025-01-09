package com.fuas.providers_access_platform.model;

import java.util.List;

public class Domain {
    private Integer domainId;
    private String domainName;
    private List<RoleOffer> roleOffer;

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

    public List<RoleOffer> getRoleOffer() {
        return roleOffer;
    }

    public void setRoleOffer(List<RoleOffer> roleOffer) {
        this.roleOffer = roleOffer;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                ", roleOffer=" + roleOffer +
                '}';
    }
}
