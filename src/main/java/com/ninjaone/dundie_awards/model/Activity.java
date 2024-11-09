package com.ninjaone.dundie_awards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "occured_at")
    private LocalDateTime occuredAt;

    @Column(name = "event")
    @Enumerated(EnumType.STRING)
    private EventEnum event;

    @Column(name = "org_id")
    private Long orgId;



    //add orgId field that doesn't save to the database, update params in MessageBroker

    public Activity() {
    }

    public Activity(LocalDateTime localDateTime, EventEnum event, Long orgId) {
        this.occuredAt = localDateTime;
        this.event = event;
        this.orgId = orgId;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public EventEnum getEvent() {
        return event;
    }

    public Long getId() { return id;}

    public Long getOrgId() { return orgId; }

}
