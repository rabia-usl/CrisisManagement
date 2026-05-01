package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.RequestResourceMatch;
import com.crisis.crisismanagement.repository.RequestResourceMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class RequestResourceMatchController {

    @Autowired
    private RequestResourceMatchRepository matchRepository;

    @GetMapping
    public List<RequestResourceMatch> getAll() {
        return matchRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RequestResourceMatch match) {
        match.setMatchDate(LocalDateTime.now());
        return ResponseEntity.ok(matchRepository.save(match));
    }

    @GetMapping("/request/{requestId}")
    public List<RequestResourceMatch> getByRequest(@PathVariable int requestId) {
        return matchRepository.findByRequestId(requestId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<RequestResourceMatch> getByResource(@PathVariable int resourceId) {
        return matchRepository.findByResourceId(resourceId);
    }
}