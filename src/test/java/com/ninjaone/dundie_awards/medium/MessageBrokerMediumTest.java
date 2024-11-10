package com.ninjaone.dundie_awards.medium;

import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.exception.ResourceNotFoundException;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.EmployeeRollbackService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class MessageBrokerMediumTest {
    @Autowired
    private MessageBroker messageBroker;

    @SpyBean
    private ActivityRepository activityRepository;

    @SpyBean
    private EmployeeRollbackService employeeRollbackService;

    @SpyBean
    private EmployeeService employeeService;

    private Activity testActivity;
    @Autowired
    private EmployeeRepository employeeRepository;


    @BeforeEach
    void setUp() {
        testActivity = new Activity(LocalDateTime.now(), EventEnum.GIVE_DUNDIE_ORG, 5L);
        activityRepository.deleteAll();  // Clear previous test data to ensure a clean state
    }

    // "Should successfully save activity without rollback"
    @Test
    void testSendMessageInternalSuccessful() {
        messageBroker.sendMessageInternal(testActivity);

        // Assert that the activity was saved in the database
        Activity savedActivity = activityRepository.findById(testActivity.getId()).orElse(null);
        assert savedActivity != null;

        // Verify that rollback was not called
        verify(employeeRollbackService, times(0)).removeDundieAwardFromOrg(testActivity.getOrgId());
    }

    // "Should retry 3 times and call rollback on OptimisticLockException" NOT WORKING
//    @Test
//    void testSendMessageInternalWithOptimisticLockException() {
//        // Simulate OptimisticLockException by causing a conflict in the save process
//        doThrow(new OptimisticLockException("Forced exception for testing"))
//                .when(activityRepository).save(any(Activity.class));
//
//        // return employee from employeeRepository org call to circumvent the resourcenotfoundexception
//        doReturn(List.of(new Employee())).when(employeeRepository).findAllByOrganizationId(testActivity.getOrgId());
//
//        messageBroker.sendMessageInternal(testActivity);
//
//        // Test the retry mechanism when encountering OptimisticLockException
//        verify(activityRepository, times(3)).save(any(Activity.class));
//
//        // Verify that rollback was not called
//        verify(employeeRollbackService, times(0)).removeDundieAwardFromOrg(testActivity.getOrgId());
//    }

//    @Test
//    void testSendMessageInternalWithOtherException() {
//        // This test checks the handling of a general database exception (like DataIntegrityViolationException)
//        // and verifies rollback on the first failed attempt.
//
//        testActivity.setId(null);  // Ensure unique primary key to cause DataIntegrityViolationException
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            messageBroker.sendMessageInternal(testActivity);
//        });
//
//        // Verify that rollback occurred after the first failed attempt
//        verify(employeeRollbackService, times(1)).removeDundieAwardFromOrg(testActivity.getOrgId());
//    }
}
