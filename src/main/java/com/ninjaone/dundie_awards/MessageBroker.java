package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.service.ActivityService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Component
public class MessageBroker {

    private List<Activity> messages = new LinkedList<>();
    private final ActivityRepository activityRepository;
    private final EmployeeRepository employeeRepository;
    private final AwardsCache awardsCache;
    private final ThreadPoolTaskExecutor taskExecutor;
    private static final Logger logger = LoggerFactory.getLogger(MessageBroker.class);

    public MessageBroker(ActivityRepository activityRepository, EmployeeRepository employeeRepository, AwardsCache awardsCache, ThreadPoolTaskExecutor taskExecutor) {
        this.activityRepository = activityRepository;
        this.employeeRepository = employeeRepository;
        this.awardsCache = awardsCache;
        this.taskExecutor = taskExecutor;
    }

    @Async
    @Transactional
    public void sendMessage(Activity message, Optional<Long> orgId) {

        logger.info("Asyncronously creating activity for {}, occurredAt : {}", message.getEvent(),message.getOccuredAt());
        try {
            activityRepository.save(message);

        } catch (Exception e) {
            logger.error("Error occured in creating activity {} for orgId {} \n {} ",
                    message.getEvent(), e.getMessage(), e.getMessage());
            orgId.ifPresent(id -> { // rolling back update in error for requirement
                int employeesUpdated = employeeRepository.decrementDundieAwardsByOrgId(id);
                awardsCache.setTotalAwards(awardsCache.getTotalAwards() - employeesUpdated);
                logger.error("Rolled back {} employees dudnie award update in org with orgId {}", employeesUpdated, orgId);
            });
        }
    }

    public List<Activity> getMessages(){

        // difficult to get activity from here

//        BlockingQueue<Runnable> queue = executor.getQueue();
//
//        System.out.println("Tasks currently in the queue: " + queue.size());
//
//        for (Runnable task : queue) {
//            System.out.println("Queued task: " + task);
//        }

        return messages;
    }
}
