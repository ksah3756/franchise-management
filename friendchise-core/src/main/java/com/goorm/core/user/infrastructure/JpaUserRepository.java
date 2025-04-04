package com.goorm.core.user.infrastructure;

import com.goorm.core.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
