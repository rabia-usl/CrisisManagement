package com.crisis.crisismanagement.repository;

import com.crisis.crisismanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByIdentityNumberAndUserPassword(
            String identityNumber,
            String userPassword
    );

    User findByIdentityNumber(String identityNumber);
}