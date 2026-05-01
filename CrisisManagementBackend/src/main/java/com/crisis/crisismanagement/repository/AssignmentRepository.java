package com.crisis.crisismanagement.repository;

import com.crisis.crisismanagement.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    List<Assignment> findByRequestId(Integer requestId);

    List<Assignment> findByStatus(String status);

    List<Assignment> findByVolunteerId(Integer volunteerId);

    List<Assignment> findByVolunteerIdAndStatus(Integer volunteerId, String status);
}