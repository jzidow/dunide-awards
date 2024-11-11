package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.ActivityRunnable;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.service.EmployeeRollbackService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Component
public class MessageBroker {

    private final EmployeeRollbackService employeeRollbackService;
    private final ActivityRepository activityRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private static final Logger logger = LoggerFactory.getLogger(MessageBroker.class);
    private static final int MAX_RETRIES = 3;

    public MessageBroker(EmployeeRollbackService employeeRollbackService,
                         ActivityRepository activityRepository,
                         @Qualifier("customTaskExecutor")ThreadPoolTaskExecutor taskExecutor) {
        this.employeeRollbackService = employeeRollbackService;
        this.activityRepository = activityRepository;
        this.taskExecutor = taskExecutor;
    }

    public void sendMessage(Activity message){
        logger.info("Queuing activity type: {}, occurredAt: {}, orgId: {}",
                message.getEvent(), message.getOccuredAt(), message.getOrgId());
        ActivityRunnable activityRunnable = new ActivityRunnable(message, this);
        taskExecutor.execute(activityRunnable);
        logger.info("Queued activity type: {}, occurredAt: {}, orgId: {}",
                message.getEvent(), message.getOccuredAt(), message.getOrgId());
    }

    @Transactional
    public void consumeMessage(Activity message){
        logger.info("Message read to create activity type: {}, occurredAt: {}, orgId: {}",
                message.getEvent(),message.getOccuredAt(), message.getOrgId());
        boolean succeeded = false;
        int attempt = 0;
        while (!succeeded && attempt < MAX_RETRIES) {
            ++attempt;
            logger.info("Creating activity with type: {}, occurredAt: {}, orgId: {} on attempt: {}/{}",
                    message.getEvent(), message.getOccuredAt(), message.getOrgId(), attempt, MAX_RETRIES);
            try {
                message = activityRepository.save(message);
                succeeded = true;
                logger.info("Sucessfully created activity with id: {}, type: {}, occurredAt: {}, orgId: {} on attempt: {}/{}",
                        message.getId(), message.getEvent(), message.getOccuredAt(), message.getOrgId(), attempt, MAX_RETRIES);

            } catch(OptimisticLockException e) {
                if (attempt >= MAX_RETRIES) {
                    logger.warn("Optimistic Lock Detected on final attempt {}/{} \n {} \n {}", attempt, MAX_RETRIES, e.getMessage(), e.getStackTrace());
                } else {
                    logger.warn("Optimistic Lock Detected on attempt {}/{}, retrying ...\n {} \n {}", attempt, MAX_RETRIES, e.getMessage(), e.getStackTrace());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        logger.error("Sleeping thread interrupted when waiting to retry creating activity {} for orgId {} on attempt {}/{} \n {} \n {} ",
                                message.getEvent(), e.getMessage(), attempt, MAX_RETRIES, e.getMessage(), e.getStackTrace());
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred in creating activity {} for orgId {} on attempt {}/{} \n {} \n {} ",
                        message.getEvent(), e.getMessage(), attempt, MAX_RETRIES, e.getMessage(), e.getStackTrace());
                break;
            }
        }

        if (!succeeded) {
            // rolling back update in error for requirement
            if (message.getEvent() == EventEnum.GIVE_DUNDIE_ORG){
                employeeRollbackService.removeDundieAwardFromOrg(message.getOrgId());
            }
        }
    }

    public List<Activity> getMessages(){
        ThreadPoolExecutor executor = taskExecutor.getThreadPoolExecutor();
        BlockingQueue<Runnable> queue = executor.getQueue();

        return queue.stream()
                .filter(task -> task instanceof ActivityRunnable)
                .map(task -> ((ActivityRunnable) task).getActivity())
                .collect(Collectors.toList());

    }
}
