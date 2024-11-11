package com.ninjaone.dundie_awards.small;

import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.EmployeeRollbackService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageBrokerSmallTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private EmployeeRollbackService employeeRollbackService;

    @InjectMocks
    private MessageBroker messageBroker;

    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = new Activity(LocalDateTime.now(), EventEnum.GIVE_DUNDIE_ORG, 5L); // Configure test Activity instance
    }

    // Upon success of creating activity
    // Assert.save called once
    // Assert no rollback
    @Test
    @DisplayName("Sucessfuly Send Message")
    void testSendMessageInternalSuccessful() {
        // Assuming sendMessageInternal method calls repository methods
        doReturn(testActivity).when(activityRepository).save(any(Activity.class));
        messageBroker.sendMessageInternal(testActivity);
        verify(activityRepository, times(1)).save(testActivity);
        verify(employeeRollbackService, times(0)).removeDundieAwardFromOrg(testActivity.getOrgId());
    }

    // Upon optimistic lock exception from creating activity
    // Assert .save called 3 times
    // Assert 1 rollback occurred
    @Test
    @DisplayName("Send Message Retry for Lock Exception")
    void testSendMessageInternalWithOptimisticLockException() {
        // Simulate OptimisticLockException
        doThrow(new OptimisticLockException("TEST simulated optimistic lock exception"))
                .when(activityRepository).save(any(Activity.class));
        messageBroker.sendMessageInternal(testActivity);
        verify(activityRepository, times(3)).save(testActivity);
        verify(employeeRollbackService, times(1)).removeDundieAwardFromOrg(testActivity.getOrgId());
    }

    // Upon other exception from creating activity
    // Assert .save(activity) called once, we only retry on optimistic lock exceptions
    // Assert 1 rollback occurred
    @Test
    @DisplayName("Send Message No Retry for Exception")
    void testSendMessageInternalWithException() {
        // Simulate RuntimeException
        doThrow(new RuntimeException("TEST simulated optimistic lock exception"))
                .when(activityRepository).save(any(Activity.class));
        messageBroker.sendMessageInternal(testActivity);
        verify(activityRepository, times(1)).save(testActivity);
        verify(employeeRollbackService, times(1)).removeDundieAwardFromOrg(testActivity.getOrgId());
    }
}
