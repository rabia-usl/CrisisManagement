package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.Assignment;
import com.crisis.crisismanagement.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;


    @GetMapping
    public List<Assignment> getAll() {
        return assignmentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Assignment assignment) {
        assignment.setAssignmentId(null);  // bunu ekle
        assignment.setStatus("IN_PROGRESS");
        return ResponseEntity.ok(assignmentRepository.save(assignment));
    }

    @GetMapping("/request/{requestId}")
    public List<Assignment> getByRequest(@PathVariable int requestId) {
        return assignmentRepository.findByRequestId(requestId);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/volunteer/{volunteerId}")
    public List<Map<String, Object>> getByVolunteer(@PathVariable int volunteerId) {
        String sql = """
            SELECT a.assignmentid, a.requestid, a.volunteerid, a.quantity, a.status,
                   r.category, r.description, r.urgencylevel, r.vulnerablecount, r.times
            FROM assignments a
            JOIN request r ON a.requestid = r.requestid
            WHERE a.volunteerid = ?
            ORDER BY a.assignmentid DESC
            """;
        return jdbcTemplate.queryForList(sql, volunteerId);
    }

    @GetMapping("/volunteer/{volunteerId}/status/{status}")
    public List<Assignment> getByVolunteerAndStatus(
            @PathVariable int volunteerId,
            @PathVariable String status) {
        return assignmentRepository.findByVolunteerIdAndStatus(volunteerId, status);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestParam String status) {
        return assignmentRepository.findById(id).map(assignment -> {
            assignment.setStatus(status);
            return ResponseEntity.ok(assignmentRepository.save(assignment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<?> claimAssignment(
            @PathVariable int id,
            @RequestParam int volunteerId) {
        return assignmentRepository.findById(id).map(assignment -> {
            assignment.setVolunteerId(volunteerId);
            assignment.setStatus("IN_PROGRESS");
            return ResponseEntity.ok(assignmentRepository.save(assignment));
        }).orElse(ResponseEntity.notFound().build());


    }

}