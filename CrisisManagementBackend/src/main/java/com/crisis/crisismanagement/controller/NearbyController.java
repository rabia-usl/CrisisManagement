package com.crisis.crisismanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nearby")
public class NearbyController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/requests")
    public List<Map<String, Object>> getNearbyRequests(
            @RequestParam double lat,
            @RequestParam double lng) {
        String sql = """
                SELECT r.requestid, r.category, r.urgencylevel,
                       r.description, r.status, r.vulnerablecount,
                       ST_Y(r.requestlocation::geometry) AS lat,
                       ST_X(r.requestlocation::geometry) AS lng,
                       ST_Distance(
                           r.requestlocation::geography,
                           ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                       ) AS distance_meters
                FROM request r
                WHERE r.requestlocation IS NOT NULL
                ORDER BY distance_meters ASC
                LIMIT 100
                """;
        return jdbcTemplate.queryForList(sql, lng, lat);
    }

    @GetMapping("/resources")
    public List<Map<String, Object>> getNearbyResources(
            @RequestParam double lat,
            @RequestParam double lng) {
        String sql = """
                SELECT r.resourceid, r.category,
                       r.currentquantity, r.initialquantity,
                       u.username AS provider_name,
                       ST_Y(r.resourcelocation::geometry) AS lat,
                       ST_X(r.resourcelocation::geometry) AS lng,
                       ST_Distance(
                           r.resourcelocation::geography,
                           ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                       ) AS distance_meters
                FROM resources r
                JOIN users u ON r.providerid = u.userid
                WHERE r.resourcelocation IS NOT NULL
                AND r.currentquantity > 0
                ORDER BY distance_meters ASC
                LIMIT 100
                """;
        return jdbcTemplate.queryForList(sql, lng, lat);
    }

    @GetMapping("/volunteers")
    public List<Map<String, Object>> getNearbyVolunteers(
            @RequestParam double lat,
            @RequestParam double lng) {
        String sql = """
                SELECT u.userid, u.username, u.phonenumber,
                       ST_Y(u.userlocation::geometry) AS lat,
                       ST_X(u.userlocation::geometry) AS lng,
                       ST_Distance(
                           u.userlocation::geography,
                           ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                       ) AS distance_meters
                FROM users u
                WHERE u.userrole = 'Volunteer'
                AND u.userlocation IS NOT NULL
                ORDER BY distance_meters ASC
                LIMIT 100
                """;
        return jdbcTemplate.queryForList(sql, lng, lat);
    }

    @GetMapping("/critical-resources")
    public List<Map<String, Object>> getCriticalResources() {
        String sql = """
                SELECT r.resourceid, r.category,
                       r.currentquantity, r.initialquantity,
                       (r.currentquantity::float / NULLIF(r.initialquantity, 0) * 100) AS stock_percentage
                FROM resources r
                WHERE r.currentquantity::float / NULLIF(r.initialquantity, 0) < 0.3
                ORDER BY stock_percentage ASC
                """;
        return jdbcTemplate.queryForList(sql);
    }

    @GetMapping("/unfulfilled-requests")
    public List<Map<String, Object>> getUnfulfilledRequests() {
        String sql = """
                SELECT r.requestid, r.category, r.urgencylevel,
                       r.description, r.times, r.vulnerablecount,
                       EXTRACT(EPOCH FROM (NOW() - r.times)) / 60 AS minutes_waiting
                FROM request r
                LEFT JOIN requestresourcematches m ON r.requestid = m.requestid
                WHERE r.urgencylevel = 3
                AND r.status = 'Pending'
                AND m.matchid IS NULL
                AND r.times >= NOW() - INTERVAL '30 minutes'
                ORDER BY r.times ASC
                """;
        return jdbcTemplate.queryForList(sql);
    }
}