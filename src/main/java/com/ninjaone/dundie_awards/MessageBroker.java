package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.ActivityRunnable;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Component
public class MessageBroker {

    private List<Activity> messages = new LinkedList<>();
    private final ActivityRepository activityRepository;
    private final EmployeeRepository employeeRepository;
    private final AwardsCache awardsCache;
    private final ThreadPoolTaskExecutor taskExecutor;
    private static final Logger logger = LoggerFactory.getLogger(MessageBroker.class);
    private static final int MAX_RETRIES = 3;

    public MessageBroker(ActivityRepository activityRepository,
                         EmployeeRepository employeeRepository,
                         AwardsCache awardsCache,
                         @Qualifier("customTaskExecutor")ThreadPoolTaskExecutor taskExecutor) {
        this.activityRepository = activityRepository;
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
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
    public void sendMessageInternal(Activity message) {

        logger.info("Asyncronously creating activity type: {}, occurredAt: {}, orgId: {}",
                message.getEvent(),message.getOccuredAt(), message.getOccuredAt());
        boolean succeeded = false;

        int attempt = 0;
        while (!succeeded && attempt < MAX_RETRIES) {
            try {
                ++attempt;
                message = activityRepository.save(message);
                succeeded = true;
                logger.info("Sucessfully created activity with id: {}, type: {}, occurredAt: {}, orgId: {}",
                        message.getId(), message.getEvent(), message.getOccuredAt(), message.getOrgId());
            } catch(OptimisticLockException e) {
                if (attempt >= MAX_RETRIES) {
                    logger.warn("Optimistic Lock Detected on final attempt {}/{}", attempt, MAX_RETRIES);
                } else {
                    logger.warn("Optimistic Lock Detected on attempt {}/{}, retrying ...", attempt, MAX_RETRIES);
                }

            } catch (Exception e) {
                logger.error("Error occured in creating activity {} for orgId {} \n {} ",
                        message.getEvent(), e.getMessage(), e.getMessage());
                break;
            }
        }

        if (!succeeded) {
            // rolling back update in error for requirement
            int employeesUpdated = employeeRepository.decrementDundieAwardsByOrgId(message.getOrgId());
            awardsCache.setTotalAwards(awardsCache.getTotalAwards() - employeesUpdated);
            logger.error("Rolled back {} employees dundie award updates in org with orgId {}",
                    employeesUpdated, message.getOrgId());
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
