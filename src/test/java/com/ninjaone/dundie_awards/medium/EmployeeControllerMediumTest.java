package com.ninjaone.dundie_awards.medium;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerMediumTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EmployeeService employeeService;

    private Employee testEmployee;
    private Organization testOrg;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        activityRepository.deleteAll();

        testOrg = new Organization("Test Organization");
        testEmployee = new Employee("John", "Doe", testOrg);

        testOrg = organizationRepository.save(testOrg);
        testEmployee = employeeRepository.save(testEmployee);
    }

    @Test
    @DisplayName("GET /employees - Success")
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEmployee.getId()))
                .andExpect(jsonPath("$[0].firstName").value(testEmployee.getFirstName()))
                .andExpect(jsonPath("$[0].organization.name").value(testEmployee.getOrganization().getName()));
    }

    @Test
    @DisplayName("POST /employees - Success")
    void testCreateEmployee() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Jane\", \"organization\": {\"id\":  1, \"name\": \"Test Organization\" }}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.organization.name").value("Test Organization"));
    }

    @Test
    @DisplayName("GET /employees/{id} - Success")
    void testGetEmployeeById() throws Exception {
        mockMvc.perform(get("/employees/{id}", testEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value(testEmployee.getFirstName()))
                .andExpect(jsonPath("$.organization.name").value(testEmployee.getOrganization().getName()));
    }

    @Test
    @DisplayName("PUT /employees/{id} - Success")
    void testUpdateEmployee() throws Exception {
        mockMvc.perform(put("/employees/{id}", testEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Jake-Updated\", \"lastName\":  \"Doe-Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value("Jake-Updated"))
                .andExpect(jsonPath("$.lastName").value("Doe-Updated"));
    }

    @Test
    @DisplayName("DELETE /employees/{id} - Success")
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/{id}", testEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Employee with id: " + testEmployee.getId() + " deleted']").value(true));

        // Ensure the employee is removed from the database
        assert employeeRepository.findById(testEmployee.getId()).isEmpty();
    }

    @Test
    @DisplayName("POST /give-dundie-awards/{orgId} - Success")
    void testGiveDundieAwards() throws Exception {
        Long orgId = testEmployee.getId();  // Assume employee ID can represent organization ID in this context

        mockMvc.perform(post("/give-dundie-awards/{orgId}", testOrg.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value("GIVE_DUNDIE_ORG"))
                .andExpect(jsonPath("$.orgId").value(testOrg.getId()));

        // Verify the activity is saved in the database
        Activity savedActivity = activityRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        assert savedActivity != null;
        assert savedActivity.getOrgId().equals(testOrg.getId());
    }
}
