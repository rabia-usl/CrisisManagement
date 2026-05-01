package com.crisis.crisismanagement.repository;

import com.crisis.crisismanagement.model.AidRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<AidRequest, Integer> {

    List<AidRequest> findByUrgencyLevelGreaterThanEqual(Integer level);

    List<AidRequest> findByVictimId(Integer victimId);

    List<AidRequest> findByStatus(String status);
}