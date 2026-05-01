package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.Assignment;
import com.crisis.crisismanagement.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
        assignment.setStatus("PENDING");
        return ResponseEntity.ok(assignmentRepository.save(assignment));
    }

    @GetMapping("/request/{requestId}")
    public List<Assignment> getByRequest(@PathVariable int requestId) {
        return assignmentRepository.findByRequestId(requestId);
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
}