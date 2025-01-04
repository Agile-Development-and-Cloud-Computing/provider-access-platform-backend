package com.fuas.providers_access_platform.controller;


import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.EmployeeResponse;
import com.fuas.providers_access_platform.model.Employee;
import com.fuas.providers_access_platform.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // API to get the list of employees
    @GetMapping("/{providerId}")
    public CommonResponse getEmployees(@PathVariable("providerId") Integer providerId) {
        List<Map<String, Object>> employees = employeeService.getEmployees(providerId);
        return new CommonResponse(true, "Employees retrieved successfully", employees);
    }

    // API to add a new employee
    @PostMapping("/add")
    public CommonResponse addEmployee(@RequestBody Employee employee) {
        CommonResponse response = employeeService.addEmployee(employee);
        return response;
    }

    // API to update an employee's details
    @PutMapping("/update/{employeeId}")
    public CommonResponse updateEmployee(@PathVariable("employeeId") Integer employeeId, @RequestBody Employee employee) {
        CommonResponse response = employeeService.updateEmployee(employeeId, employee);
        return response;
    }

    // API to remove an employee
    @DeleteMapping("/delete/{employeeId}")
    public CommonResponse removeEmployee(@PathVariable("employeeId") Integer employeeId) {
        CommonResponse response = employeeService.removeEmployee(employeeId);
        return response;
    }


    @PostMapping("/upload")
    public CommonResponse uploadEmployeeProfile( @RequestBody Employee employeeRequest) {
        CommonResponse response = employeeService.uploadProfile(employeeRequest);
        return response;
    }


    @GetMapping("/suggestions")
    public CommonResponse <List<Map<String, Object>>> getEmployeeSuggestions(@RequestParam String knowledgeKeyword) {
        return employeeService.getSuggestions(knowledgeKeyword);
    }
}
