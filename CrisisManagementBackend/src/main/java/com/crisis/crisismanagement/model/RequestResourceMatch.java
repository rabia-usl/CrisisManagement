package com.crisis.crisismanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requestresourcematches")
public class RequestResourceMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchid")
    private Integer matchId;

    @Column(name = "requestid")
    private Integer requestId;

    @Column(name = "resourceid")
    private Integer resourceId;

    @Column(name = "matchdate")
    private LocalDateTime matchDate;

    @Column(name = "allocatequantity")
    private Integer allocateQuantity;

    public Integer getMatchId() { return matchId; }
    public void setMatchId(Integer matchId) { this.matchId = matchId; }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Integer getResourceId() { return resourceId; }
    public void setResourceId(Integer resourceId) { this.resourceId = resourceId; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

    public Integer getAllocateQuantity() { return allocateQuantity; }
    public void setAllocateQuantity(Integer allocateQuantity) { this.allocateQuantity = allocateQuantity; }
}