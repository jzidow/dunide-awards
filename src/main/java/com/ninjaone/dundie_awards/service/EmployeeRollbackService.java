package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.exception.ResourceNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeRollbackService {

    private final EmployeeRepository employeeRepository;
    private final AwardsCache awardsCache;
    private final static Logger logger = LoggerFactory.getLogger(EmployeeRollbackService.class);

    public EmployeeRollbackService(EmployeeRepository employeeRepository, AwardsCache awardsCache) {
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
    }

    @Retryable(
            value = { OptimisticLockException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    @Transactional
    public void removeDundieAwardFromOrg(Long orgId){

        logger.info("ROLLBACK: removing one dundie award to each employee in org with orgId {}", orgId);
        List<Employee> employees = employeeRepository.findAllByOrganizationId(orgId);

        if (employees.isEmpty()) {
            logger.error("Unable to find employees in org with id {} to remove a dundie award", orgId);
            throw new ResourceNotFoundException("Organization not found with ID: " + orgId);
        }
        for (Employee employee : employees) {
            employee.setDundieAwards(employee.getDundieAwards() - 1);
        }
        awardsCache.subtractAwards(employees.size());
    }
}
