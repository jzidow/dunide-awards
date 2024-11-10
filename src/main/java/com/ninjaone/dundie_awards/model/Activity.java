package com.ninjaone.dundie_awards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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

    @Version
    private Integer version;

    public Activity() {
    }

    public Activity(LocalDateTime occurredAt, EventEnum event, Long orgId) {
        this.occuredAt = occurredAt;
        this.event = event;
        this.orgId = orgId;
    }

    public Long getId() { return id;}

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public EventEnum getEvent() {
        return event;
    }

    public Long getOrgId() { return orgId; }
}
