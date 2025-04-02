package com.goorm.api.user.infrastructure;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public User save(User user) {
        User savedUser = User.builder()
                .id(sequence.getAndIncrement())
                .username(user.getUsername())
                .password(user.getPassword())
                .userRole(user.getUserRole())
                .build();
        if (existsByUsername(savedUser.getUsername())) {
            throw new RuntimeException("이미 존재하는 유저 이름입니다.");
        }
        users.add(savedUser);
        return savedUser;
    }

    private boolean existsByUsername(String username) {
        return users.stream()
                .anyMatch(existingUser -> existingUser.getUsername().equals(username));
    }

    @Override
    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}
