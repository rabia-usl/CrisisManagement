package com.crisis.crisismanagement.repository;

import com.crisis.crisismanagement.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    List<Resource> findByCategory(String category);

    List<Resource> findByProviderId(Integer providerId);

    List<Resource> findByCurrentQuantityGreaterThan(Integer quantity);
}