package com.crisis.crisismanagement.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Integer userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "userrole")
    private String userRole;

    @Column(name = "userpassword")
    private String userPassword;

    @Column(name = "identitynumber")
    private String identityNumber;

    @Column(name = "userlocation", columnDefinition = "geometry(Point,4326)")
    private Point userLocation;

    @Transient
    private Double latitude;

    @Transient
    private Double longitude;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public Point getUserLocation() { return userLocation; }
    public void setUserLocation(Point userLocation) { this.userLocation = userLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}