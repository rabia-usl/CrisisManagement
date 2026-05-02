package com.crisis.crisismanagement.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resourceid")
    private Integer resourceId;

    @Column(name = "providerid")
    private Integer providerId;

    @Column(name = "category")
    private String category;

    @Column(name = "initialquantity")
    private Integer initialQuantity;

    @Column(name = "currentquantity")
    private Integer currentQuantity;

    @Column(name = "resourcelocation", columnDefinition = "geometry(Point,4326)")
    private Point resourceLocation;

    @Transient
    private Double latitude;

    @Transient
    private Double longitude;

    public Integer getResourceId() { return resourceId; }
    public void setResourceId(Integer resourceId) { this.resourceId = resourceId; }

    public Integer getProviderId() { return providerId; }
    public void setProviderId(Integer providerId) { this.providerId = providerId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }

    public Integer getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Integer currentQuantity) { this.currentQuantity = currentQuantity; }

    public Point getResourceLocation() { return resourceLocation; }
    public void setResourceLocation(Point resourceLocation) { this.resourceLocation = resourceLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}