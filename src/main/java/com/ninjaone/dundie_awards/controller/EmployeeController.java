package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // get all employees
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Handling GET request for /employees - getAllEmployees()");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // create employee rest api
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        logger.info("Handling POST request for /employees - createEmployee(employee)");
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    // get employee by id rest api
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        logger.info("Handling GET request for /employees/id - getEmployeeById(id = {})", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // update employee rest api
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        logger.info("Handling PUT request for /employees/id - updateEmployee(id = {}, employeeDetails)", id);
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDetails));
    }

    // delete employee rest api
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
        logger.info("Handling DELETE request for /employees/id - deleteEmployee(id = {})", id);

        employeeService.deleteEmployee(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("Employee with id: " + id + " deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/give-dundie-awards/{orgId}")
    public ResponseEntity<Activity> giveDundieAwards(@PathVariable Long orgId) {
        logger.info("Handling POST request for /give-dundie-awards/{organizationId} - giveDundieAwards(orgId = {})", orgId);
        Activity activity = employeeService.giveDundieAwardToOrg(orgId);
        return ResponseEntity.ok(activity);
    }

}