package com.ninjaone.dundie_awards.medium;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.EmployeeRollbackService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmployeeRollbackServiceMediumTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AwardsCache awardsCache;

    @SpyBean
    private EmployeeRollbackService employeeRollbackService;

    private Organization testOrg1;
    private Organization testOrg2;
    private Organization testOrg3;

    @BeforeEach
    void setUp() {
        testOrg1 = orgRepository.save(new Organization("Test Organization 1"));
        testOrg2 = orgRepository.save(new Organization("Test Organization 2"));
        testOrg3 = orgRepository.save(new Organization("Test Organization 3"));

        Employee employee1org1 = new Employee("Alice", "Smith", testOrg1);
        Employee employee2org1 = new Employee("Jake", "Jazz", testOrg1);
        Employee employee1org2 = new Employee("Bob", "Drinkwater", testOrg2);
        Employee employee1org3 = new Employee("Charlie", "Cool", testOrg3);

        employeeRepository.saveAll(List.of(employee1org1, employee2org1, employee1org2, employee1org3));
    }

    @Test
    @DisplayName("Rollback Dundie Award")
    void testRemoveDundieAwardFromOrg() throws InterruptedException {
        //add 2 dundie awards to org1
        employeeService.giveDundieAwardToOrg(testOrg1.getId());
        employeeService.giveDundieAwardToOrg(testOrg1.getId());

        //add 1 dundie to org2
        employeeService.giveDundieAwardToOrg(testOrg2.getId());

        //assert correct dundie awards in employee repo and awards cache
        List<Employee> org1Employees = employeeRepository.findAllByOrganizationId(testOrg1.getId());
        assertEquals(2, org1Employees.get(0).getDundieAwards());
        assertEquals(2, org1Employees.get(1).getDundieAwards());
        assertEquals(1, employeeRepository.findAllByOrganizationId(testOrg2.getId()).get(0).getDundieAwards());
        assertEquals(0, employeeRepository.findAllByOrganizationId(testOrg3.getId()).get(0).getDundieAwards());

        assertEquals(5, awardsCache.getTotalAwards());

        // Perform the rollback for org1
        employeeRollbackService.removeDundieAwardFromOrg(testOrg1.getId());

        org1Employees = employeeRepository.findAllByOrganizationId(testOrg1.getId());

        // Assert that dundieAward is decremented for each employee in ORG_ID and
        assertEquals(1, org1Employees.get(0).getDundieAwards());
        assertEquals(1, org1Employees.get(1).getDundieAwards());
        assertEquals(1, employeeRepository.findAllByOrganizationId(testOrg2.getId()).get(0).getDundieAwards());
        assertEquals(0, employeeRepository.findAllByOrganizationId(testOrg3.getId()).get(0).getDundieAwards());
        assertEquals(3, awardsCache.getTotalAwards());
    }
}