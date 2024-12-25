package com.fuas.providers_access_platform.dto;

import org.springframework.data.relational.core.sql.In;

public class EmployeeResponse {
    private Integer employeeId;
    private String employeeName;
    private String knowledge;
    private String experience;
    private String skills;

    // Constructor
    public EmployeeResponse(Integer employeeId, String employeeName, String knowledge, String experience, String skills) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.knowledge = knowledge;
        this.experience = experience;
        this.skills = skills;
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

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}