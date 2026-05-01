package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.AidRequest;
import com.crisis.crisismanagement.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    // Tüm requestleri getir
    @GetMapping
    public List<AidRequest> getAll() {
        return requestRepository.findAll();
    }

    // Yeni request oluştur
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AidRequest request) {
        request.setStatus("PENDING");
        request.setTimes(LocalDateTime.now());
        return ResponseEntity.ok(requestRepository.save(request));
    }

    // Belirli bir requesti getir
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return requestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Kurban'a göre requestleri getir
    @GetMapping("/victim/{victimId}")
    public List<AidRequest> getByVictim(@PathVariable int victimId) {
        return requestRepository.findByVictimId(victimId);
    }

    // Aciliyet seviyesine göre getir
    @GetMapping("/urgent/{level}")
    public List<AidRequest> getUrgent(@PathVariable int level) {
        return requestRepository.findByUrgencyLevelGreaterThanEqual(level);
    }

    // Status güncelle
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestParam String status) {
        return requestRepository.findById(id).map(request -> {
            request.setStatus(status);
            return ResponseEntity.ok(requestRepository.save(request));
        }).orElse(ResponseEntity.notFound().build());
    }
}