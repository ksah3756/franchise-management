package com.goorm.friendchise.domain.headquarter.insfrastructure;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.notification.domain.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeHeadquarterRepository implements HeadquarterRepository {
    private final List<Headquarter> headquarters = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);
    @Override
    public Headquarter save(Headquarter headquarter) {
        Headquarter savedHeadquarter = Headquarter.builder()
                .id(sequence.getAndIncrement())
                .franchiseName(headquarter.getFranchiseName())
                .build();
        headquarters.add(savedHeadquarter);
        return savedHeadquarter;
    }

    @Override
    public Optional<Headquarter> findById(Long id) {
        return headquarters.stream()
                .filter(headquarter -> headquarter.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existsByFranchiseName(String franchiseName) {
        Optional<Headquarter> foundHeadquarter = headquarters.stream()
                .filter(headquarter -> headquarter.getFranchiseName().equals(franchiseName))
                .findFirst();
        return foundHeadquarter.isPresent();
    }

    @Override
    public void deleteById(Long id) {
        headquarters.removeIf(headquarter -> headquarter.getId().equals(id));
    }

}
