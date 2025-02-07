package com.goorm.friendchise.domain.manager.infrastructure;

import com.goorm.friendchise.domain.manager.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUsername(String username);
}