package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.exception.ResourceNotFoundException;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AwardsCache awardsCache;
    private final MessageBroker messageBroker;

    private final static Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService(EmployeeRepository employeeRepository,
                           AwardsCache awardsCache,
                           MessageBroker messageBroker) {
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
        this.messageBroker = messageBroker;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Retryable(
            value = { OptimisticLockException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        return employee;
    }


    @Retryable(
            value = { OptimisticLockException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public void deleteEmployee(Long id) {
        int deletedRows = employeeRepository.deleteEmployeeById(id);
        if (deletedRows == 0) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
    }


    @Retryable(
            value = { OptimisticLockException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public Activity giveDundieAwardToOrg(Long orgId){

        logger.info("Adding one dundie award to each employee in org with orgId {}", orgId);
        List<Employee> employees = employeeRepository.findAllByOrganizationId(orgId);

        if (employees.isEmpty()) {
            logger.error("Unable to find emplyees in org with id {}", orgId);
            throw new ResourceNotFoundException("Organization not found with ID: " + orgId);
        }

        for (Employee employee : employees) {
            employee.setDundieAwards(employee.getDundieAwards() + 1);
        }

        awardsCache.addAwards(employees.size());
        Activity activity = new Activity(LocalDateTime.now(), EventEnum.GIVE_DUNDIE_ORG, orgId);
        messageBroker.sendMessage(activity);

        return activity;
    }
}
