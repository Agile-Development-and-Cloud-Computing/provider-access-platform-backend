package com.fuas.providers_access_platform.model;

public class Employee {

    private Integer employeeId;
    private String employeeName;
    private String role;
    private String level;
    private String technologyLevel;  // Could be a comma-separated list of technologies
    private Integer providerId;
    private String resumeUrl;


    public Employee(Integer employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public Employee() {
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", role='" + role + '\'' +
                ", level='" + level + '\'' +
                ", technologyLevel='" + technologyLevel + '\'' +
                ", providerId=" + providerId +
                ", resumeUrl='" + resumeUrl + '\'' +
                '}';
    }
}
