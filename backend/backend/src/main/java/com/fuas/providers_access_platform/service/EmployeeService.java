package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Method to get the list of employees for a specific provider
    public List<Map<String, Object>> getEmployees(Integer providerId) {
        String sql = "SELECT employee_id, employee_name, role, experience_level, skills " +
                "FROM employees WHERE provider_id = ?";

        return jdbcTemplate.query(sql, new Object[]{providerId}, (rs, rowNum) -> {
            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", rs.getInt("employee_id"));
            employee.put("employeeName", rs.getString("employee_name"));
            employee.put("role", rs.getString("role"));
            employee.put("experienceLevel", rs.getString("experience_level"));
            employee.put("skills", rs.getString("skills"));
            return employee;
        });
    }

    // Method to add a new employee
    public CommonResponse addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_name, role, experience_level, skills, provider_id) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(sql,
                    employee.getEmployeeName(),
                    employee.getRole(),
                    employee.getExperienceLevel(),
                    employee.getSkills(),
                    employee.getProviderId()
            );
            return new CommonResponse(true, "Employee successfully added",employee);
        } catch (DataIntegrityViolationException e) {
            return new CommonResponse(false, "Provider ID does not exist or other database error: ",null);
        } catch (Exception e) {
            return new CommonResponse(false, "Error adding employee: " + e.getMessage(), null);
        }
    }

    // Method to update an existing employee's details
    public CommonResponse updateEmployee(Integer employeeId, Employee employee) {
        String sql = "UPDATE employees SET employee_name = ?, role = ?, experience_level = ?, skills = ? WHERE employee_id = ?";

        try {
            jdbcTemplate.update(sql,
                    employee.getEmployeeName(),
                    employee.getRole(),
                    employee.getExperienceLevel(),
                    employee.getSkills(),
                    employeeId
            );
            return new CommonResponse(true, "Employee successfully updated", null);
        } catch (Exception e) {
            return new CommonResponse(false, "Error updating employee: " + e.getMessage(), null);
        }
    }

    // Method to remove an employee from the system
    public CommonResponse removeEmployee(Integer employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try {
            jdbcTemplate.update(sql, employeeId);
            return new CommonResponse(true, "Employee successfully removed", null);
        } catch (Exception e) {
            return new CommonResponse(false, "Error removing employee: " + e.getMessage(), null);
        }

    }


    public CommonResponse uploadProfile(Employee employeeRequest) {
        // Validate the service request and employee existence
        String checkQueryServiceRequest = "SELECT COUNT(*) FROM service_requests WHERE service_id = ?";
        Integer serviceRequestCount = jdbcTemplate.queryForObject(checkQueryServiceRequest, Integer.class, employeeRequest.getServiceId());

        if (serviceRequestCount == null || serviceRequestCount == 0) {
            return new CommonResponse (false, "Invalid service request ID", null);
        }

        String checkQueryEmployee = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        Integer employeeCount = jdbcTemplate.queryForObject(checkQueryEmployee, Integer.class, employeeRequest.getEmployeeId());

        if (employeeCount == null || employeeCount == 0) {
            return new CommonResponse (false, "Invalid employee ID", null);
        }

        // Insert the uploaded profile
        String insertQuery = "INSERT INTO employee_profiles (service_request_id, employee_id, resume_url) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, employeeRequest.getServiceId(), employeeRequest.getEmployeeId(), employeeRequest.getResumeUrl());

        return new CommonResponse (true, "Profile uploaded successfully", null);
    }


    public CommonResponse <List <Map<String,Object>>> getSuggestions(String knowledgeKeyword) {
        String query = """
        SELECT 
            e.employee_id AS employee_id, 
            e.employee_name AS employee_name, 
            e.skills AS knowledge, 
            e.experience_level AS experience, 
            e.skills AS skills
        FROM employees e
        WHERE e.skills LIKE ?
        ORDER BY e.experience_level DESC
        """;

        List<Map<String,Object>> employeeSuggestions = jdbcTemplate.query(
                query,
                new Object[]{"%" + knowledgeKeyword + "%"},
                (rs, rowNum) -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("employeeId", rs.getInt("employee_id"));
                    response.put("employeeName", rs.getString("employee_name"));
                    response.put("knowledge", rs.getString("knowledge"));
                    response.put("experience", rs.getString("experience"));
                    response.put("skills", rs.getString("skills"));
                    return response;
                }
        );

        // Wrap the response in CommonResponse format
        return new CommonResponse<>(true, "Employee suggestions fetched successfully", employeeSuggestions);
    }
}
