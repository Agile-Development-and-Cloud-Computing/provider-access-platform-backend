package com.fuas.providers_access_platform.service;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Method to get the list of employees for a specific provider
    public List<Map<String, Object>> getEmployees(Integer providerId) {
        logger.info("Fetching employees for providerId: {}", providerId);
        String sql = "SELECT employee_id, employee_name, role, level, technology_level " +
                "FROM employee WHERE provider_id = ?";
        try {
            List<Map<String, Object>> employees = jdbcTemplate.query(sql, new Object[]{providerId}, (rs, rowNum) -> {
                Map<String, Object> employee = new LinkedHashMap<>();
                employee.put("employeeId", rs.getInt("employee_id"));
                employee.put("employeeName", rs.getString("employee_name"));
                employee.put("role", rs.getString("role"));
                employee.put("level", rs.getString("level"));
                employee.put("technologyLevel", rs.getString("technology_level"));
                return employee;
            });

            logger.info("Successfully retrieved {} employees for providerId: {}", employees.size(), providerId);
            return employees;
        } catch (Exception e) {
            logger.error("Error fetching employees for providerId {}: {}", providerId, e.getMessage(), e);
            throw e;
        }
    }

    // Method to add a new employee
    public CommonResponse addEmployee(Employee employee) {
        logger.info("Adding new employee: {}", employee);
        String sql = "INSERT INTO employee (employee_name, role, level, technology_level, provider_id) VALUES (?, ?, ?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, employee.getEmployeeName());
                ps.setString(2, employee.getRole());
                ps.setString(3, employee.getLevel());
                ps.setString(4, employee.getTechnologyLevel());
                ps.setLong(5, employee.getProviderId());
                return ps;
            }, keyHolder);

            Integer employeeId = keyHolder.getKey().intValue();
            employee.setEmployeeId(employeeId);

            logger.info("Employee added successfully with ID: {}", employeeId);
            return new CommonResponse(true, "Employee successfully added", employee);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Provider ID does not exist for employee: {}", employee, e);
            return new CommonResponse(false, "Provider ID does not exist or other database error", null);
        } catch (Exception e) {
            logger.error("Error adding employee: {}", employee, e);
            return new CommonResponse(false, "Error adding employee: " + e.getMessage(), null);
        }
    }

    public CommonResponse updateEmployee(Integer employeeId, Integer providerId, Employee employee) {
        logger.info("Updating employee with ID: {} for providerId: {}", employeeId, providerId);
        String sql = "UPDATE employee SET employee_name = ?, role = ?, level = ?, technology_level = ? WHERE employee_id = ?";

        try {
            int rowsUpdated = jdbcTemplate.update(sql,
                    employee.getEmployeeName(),
                    employee.getRole(),
                    employee.getLevel(),
                    employee.getTechnologyLevel(),
                    employeeId
            );

            if (rowsUpdated == 0) {
                logger.warn("No records updated for employee ID: {}", employeeId);
                return new CommonResponse(false, "Employee not found or no update required", null);
            }

            employee.setEmployeeId(employeeId);
            employee.setProviderId(providerId);
            logger.info("Employee with ID: {} successfully updated", employeeId);
            return new CommonResponse(true, "Employee successfully updated", employee);
        } catch (Exception e) {
            logger.error("Error updating employee ID: {} - {}", employeeId, e.getMessage(), e);
            return new CommonResponse(false, "Error updating employee: " + e.getMessage(), null);
        }
    }

    // Method to remove an employee from the system
    public CommonResponse removeEmployee(Integer employeeId) {
        logger.info("Removing employee with ID: {}", employeeId);
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        try {
            int rowsDeleted = jdbcTemplate.update(sql, employeeId);
            if (rowsDeleted == 0) {
                logger.warn("No employee found with ID: {}", employeeId);
                return new CommonResponse(false, "Employee not found", null);
            }
            logger.info("Employee with ID: {} successfully removed", employeeId);
            return new CommonResponse(true, "Employee successfully removed", null);
        } catch (Exception e) {
            logger.error("Error removing employee ID: {} - {}", employeeId, e.getMessage(), e);
            return new CommonResponse(false, "Error removing employee: " + e.getMessage(), null);
        }
    }


}
