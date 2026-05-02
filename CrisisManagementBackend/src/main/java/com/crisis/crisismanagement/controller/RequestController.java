package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.AidRequest;
import com.crisis.crisismanagement.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    // Point içeren AidRequest'i JSON-safe Map'e çevirir
    private Map<String, Object> toMap(AidRequest r) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", r.getRequestId());
        map.put("victimId", r.getVictimId());
        map.put("category", r.getCategory());
        map.put("urgencyLevel", r.getUrgencyLevel());
        map.put("status", r.getStatus());
        map.put("description", r.getDescription());
        map.put("vulnerableCount", r.getVulnerableCount());
        map.put("times", r.getTimes());
        return map;
    }

    // Tüm requestleri getir
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return requestRepository.findAll().stream().map(this::toMap).toList();
    }

    // Yeni request oluştur
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AidRequest request) {
        request.setRequestId(null);
        request.setStatus("PENDING");
        request.setTimes(LocalDateTime.now());

        if (request.getLatitude() != null && request.getLongitude() != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = factory.createPoint(
                    new Coordinate(request.getLongitude(), request.getLatitude())
            );
            request.setRequestLocation(point);
        }

        requestRepository.save(request);
        return ResponseEntity.ok().build();
    }

    // Belirli bir requesti getir
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return requestRepository.findById(id)
                .map(r -> ResponseEntity.ok(toMap(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Kurban'a göre requestleri getir
    @GetMapping("/victim/{victimId}")
    public List<Map<String, Object>> getByVictim(@PathVariable int victimId) {
        return requestRepository.findByVictimId(victimId).stream().map(this::toMap).toList();
    }

    // Aciliyet seviyesine göre getir
    @GetMapping("/urgent/{level}")
    public List<Map<String, Object>> getUrgent(@PathVariable int level) {
        return requestRepository.findByUrgencyLevelGreaterThanEqual(level).stream().map(this::toMap).toList();
    }

    // Status güncelle
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestParam String status) {
        return requestRepository.findById(id).map(request -> {
            request.setStatus(status);
            requestRepository.save(request);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // İptal edilen requesti sil
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (requestRepository.existsById(id)) {
            requestRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}