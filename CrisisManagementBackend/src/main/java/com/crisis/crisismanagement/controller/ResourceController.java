package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.Resource;
import com.crisis.crisismanagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    // Tüm kaynakları getir
    @GetMapping
    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    // Yeni kaynak ekle
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Resource resource) {
        resource.setCurrentQuantity(resource.getInitialQuantity());
        return ResponseEntity.ok(resourceRepository.save(resource));
    }

    // Belirli bir kaynağı getir
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return resourceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Kategoriye göre getir
    @GetMapping("/category/{category}")
    public List<Resource> getByCategory(@PathVariable String category) {
        return resourceRepository.findByCategory(category);
    }

    // Sağlayıcıya göre getir
    @GetMapping("/provider/{providerId}")
    public List<Resource> getByProvider(@PathVariable int providerId) {
        return resourceRepository.findByProviderId(providerId);
    }

    // Miktarı güncelle
    @PutMapping("/{id}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable int id,
            @RequestParam int quantity) {
        return resourceRepository.findById(id).map(resource -> {
            resource.setCurrentQuantity(quantity);
            return ResponseEntity.ok(resourceRepository.save(resource));
        }).orElse(ResponseEntity.notFound().build());
    }
}