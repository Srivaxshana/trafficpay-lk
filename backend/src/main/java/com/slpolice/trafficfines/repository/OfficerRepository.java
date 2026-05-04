package com.slpolice.trafficfines.repository;

import com.slpolice.trafficfines.model.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByUser_Username(String username);
    Optional<Officer> findByBadgeNumber(String badgeNumber);
}
