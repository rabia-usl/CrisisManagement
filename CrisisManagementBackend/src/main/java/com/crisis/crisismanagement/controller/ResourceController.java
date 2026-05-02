package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.Resource;
import com.crisis.crisismanagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    // Point içeren Resource'u JSON-safe Map'e çevirir
    private Map<String, Object> toMap(Resource r) {
        Map<String, Object> map = new HashMap<>();
        map.put("resourceId", r.getResourceId());
        map.put("providerId", r.getProviderId());
        map.put("category", r.getCategory());
        map.put("initialQuantity", r.getInitialQuantity());
        map.put("currentQuantity", r.getCurrentQuantity());
        return map;
    }

    // Tüm kaynakları getir
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return resourceRepository.findAll().stream().map(this::toMap).toList();
    }

    // Yeni kaynak ekle
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Resource resource) {
        Resource newResource = new Resource();
        newResource.setProviderId(resource.getProviderId());
        newResource.setCategory(resource.getCategory());
        newResource.setInitialQuantity(resource.getInitialQuantity());
        newResource.setCurrentQuantity(resource.getInitialQuantity());

        if (resource.getLatitude() != null && resource.getLongitude() != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = factory.createPoint(
                    new Coordinate(resource.getLongitude(), resource.getLatitude())
            );
            newResource.setResourceLocation(point);
        }

        resourceRepository.save(newResource);
        return ResponseEntity.ok().build();
    }

    // Belirli bir kaynağı getir
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return resourceRepository.findById(id)
                .map(r -> ResponseEntity.ok(toMap(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Kategoriye göre getir
    @GetMapping("/category/{category}")
    public List<Map<String, Object>> getByCategory(@PathVariable String category) {
        return resourceRepository.findByCategory(category).stream().map(this::toMap).toList();
    }

    // Sağlayıcıya göre getir
    @GetMapping("/provider/{providerId}")
    public List<Map<String, Object>> getByProvider(@PathVariable int providerId) {
        return resourceRepository.findByProviderId(providerId).stream().map(this::toMap).toList();
    }

    // Miktarı güncelle
    @PutMapping("/{id}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable int id,
            @RequestParam int quantity) {
        return resourceRepository.findById(id).map(resource -> {
            resource.setCurrentQuantity(quantity);
            resourceRepository.save(resource);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}