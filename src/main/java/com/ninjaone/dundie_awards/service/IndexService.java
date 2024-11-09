package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class IndexService {
    private final EmployeeService employeeService;
    private final ActivityService activityService;
    private final MessageBroker messageBroker;
    private final AwardsCache awardsCache;

    public IndexService(EmployeeService employeeService, ActivityService activityService, MessageBroker messageBroker, AwardsCache awardsCache) {
        this.employeeService = employeeService;
        this.activityService = activityService;
        this.messageBroker = messageBroker;
        this.awardsCache = awardsCache;
    }

    public void populateIndex(Model model){
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("queueMessages", messageBroker.getMessages());
        model.addAttribute("totalDundieAwards", awardsCache.getTotalAwards());
    }
}
