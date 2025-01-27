package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.model.Employee;
import com.fuas.providers_access_platform.service.EmployeeService;
import com.fuas.providers_access_platform.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true" ,allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    // API to get the list of employees
    @GetMapping("/{providerId}")
    public CommonResponse getEmployees(@PathVariable("providerId") Integer providerId) {
        logger.info("Received request to get employees for providerId: {}", providerId);
        List<Map<String, Object>> employees = employeeService.getEmployees(providerId);
        logger.info("Successfully retrieved {} employees for providerId: {}", employees.size(), providerId);
        return new CommonResponse(true, "Employees retrieved successfully", employees);
    }

    // API to add a new employee
    @PostMapping("/add")
    public CommonResponse addEmployee(@RequestBody Employee employee) {
        logger.info("Received request to add a new employee: {}", employee);
        CommonResponse response = employeeService.addEmployee(employee);
        if (response.isSuccess()) {
            logger.info("Employee added successfully: {}", employee);
        } else {
            logger.warn("Failed to add employee: {}", response.getMessage());
        }
        return response;
    }

    // API to update an employee's details
    @PutMapping("/update/{employeeId}/{providerId}")
    public CommonResponse updateEmployee(@PathVariable("employeeId") Integer employeeId,
                                         @PathVariable("providerId") Integer providerId,
                                         @RequestBody Employee employee) {
        logger.info("Received request to update employee with employeeId: {} for providerId: {}", employeeId, providerId);
        CommonResponse response = employeeService.updateEmployee(employeeId, providerId, employee);
        if (response.isSuccess()) {
            logger.info("Employee with employeeId: {} updated successfully", employeeId);
        } else {
            logger.warn("Failed to update employee with employeeId: {} - {}", employeeId, response.getMessage());
        }
        return response;
    }

    // API to remove an employee
    @DeleteMapping("/delete/{employeeId}")
    public CommonResponse removeEmployee(@PathVariable("employeeId") Integer employeeId) {
        logger.info("Received request to delete employee with employeeId: {}", employeeId);
        CommonResponse response = employeeService.removeEmployee(employeeId);
        if (response.isSuccess()) {
            logger.info("Employee with employeeId: {} deleted successfully", employeeId);
        } else {
            logger.warn("Failed to delete employee with employeeId: {} - {}", employeeId, response.getMessage());
        }
        return response;
    }

}
