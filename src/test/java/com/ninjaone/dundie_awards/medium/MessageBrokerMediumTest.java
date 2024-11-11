package com.ninjaone.dundie_awards.medium;

import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.EmployeeRollbackService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;

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

    private Long orgId = 5L;


    @BeforeEach
    void setUp() {
        testActivity = new Activity(LocalDateTime.now(), EventEnum.GIVE_DUNDIE_ORG, orgId);
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
//    }
//
//    @Test
//    void testSendMessageInternalWithOtherException() {
//    }
}
