package com.goorm.api.headquarter.insfrastructure;

import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeHeadquarterRepository implements HeadquarterRepository {
    private final List<Headquarter> headquarters = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);
    @Override
    public Headquarter save(Headquarter headquarter) {
        Headquarter savedHeadquarter = Headquarter.builder()
                .id(sequence.getAndIncrement())
                .franchiseName(headquarter.getFranchiseName())
                .restaurantCategory(headquarter.getRestaurantCategory())
                .restaurantSubCategory(headquarter.getRestaurantSubCategory())
            	.certificationNumber(UUID.randomUUID().toString())
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

    @Override
    public Optional<Headquarter> findByFranchiseName(String franchiseName) {
        return headquarters.stream()
                .filter(headquarter -> headquarter.getFranchiseName().equals(franchiseName))
                .findFirst();
    }

    @Override
    public void deleteAll() {
        headquarters.clear();
        sequence.set(1);
    }

}
