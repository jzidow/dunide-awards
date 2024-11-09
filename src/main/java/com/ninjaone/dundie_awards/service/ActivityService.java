package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.exception.ResourceNotFoundException;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.EventEnum;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

//    @Async
//    @Transactional
//    public void createActivityAsync(LocalDateTime occuredAt, EventEnum event, Long orgId){
//        logger.info("Asyncronously creating activity for {}, occurredAt : {}, orgId: {}", occuredAt, event, orgId);
//        try {
//            Activity activity = new Activity(occuredAt, event);
//            activityRepository.save(activity);
//        } catch (Exception e) {
//            if (orgId != null){
//                //rolling back awards distribution in the case activity creation fails
//                int employeesUpdated = employeeRepository.decrementDundieAwardsByOrgId(orgId);
//                awardsCache.setTotalAwards(awardsCache.getTotalAwards() - employeesUpdated);
//
//            }
//
//        }
//
//    }

}
