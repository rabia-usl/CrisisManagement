package com.crisis.crisismanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
public class AidRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestid")
    private Integer requestId;

    @Column(name = "victimid")
    private Integer victimId;

    @Column(name = "category")
    private String category;

    @Column(name = "urgencylevel")
    private Integer urgencyLevel;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "times")
    private LocalDateTime times;

    @Column(name = "vulnerablecount")
    private Integer vulnerableCount;

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Integer getVictimId() { return victimId; }
    public void setVictimId(Integer victimId) { this.victimId = victimId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(Integer urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimes() { return times; }
    public void setTimes(LocalDateTime times) { this.times = times; }

    public Integer getVulnerableCount() { return vulnerableCount; }
    public void setVulnerableCount(Integer vulnerableCount) { this.vulnerableCount = vulnerableCount; }
}