package com.crisis.crisismanagement.repository;

import com.crisis.crisismanagement.model.RequestResourceMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequestResourceMatchRepository extends JpaRepository<RequestResourceMatch, Integer> {

    List<RequestResourceMatch> findByRequestId(Integer requestId);

    List<RequestResourceMatch> findByResourceId(Integer resourceId);
}