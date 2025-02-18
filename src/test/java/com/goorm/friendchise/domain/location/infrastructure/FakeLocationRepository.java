package com.goorm.friendchise.domain.location.infrastructure;

import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.location.domain.Location;
import com.goorm.friendchise.domain.location.domain.LocationRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeLocationRepository implements LocationRepository
{
    private final Map<Long, Location> store = new HashMap<>(); // In-memory 저장소
    private final AtomicLong sequence = new AtomicLong(1L); // ID 자동 증가

    @Override
    public Location save(Location location) {
        if (location.getId() == null) { // 새로운 고객이면 ID 부여
            location = Location.builder()
                    .id(sequence.getAndIncrement())
                    .customer(location.getCustomer())
                    .startY(location.getStartY())
                    .startX(location.getStartX())
                    .destinationY(location.getDestinationY())
                    .destinationX(location.getDestinationX())
                    .recordedAt(location.getRecordedAt())
                    .build();
        }
        store.put(location.getId(), location);
        return location;
    }

    @Override
    public Optional<Location> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Location> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Location location) {

    }

    @Override
    public List<Location> findByCustomerUsernameOrderByRecordedAtDesc(String username) {
        return store.values().stream()
                .filter(location -> location.getCustomer().getUsername().equals(username)) // username이 일치하는 Location만 필터링
                .sorted(Comparator.comparing(Location::getRecordedAt).reversed()) // recordedAt 기준 오름차순 정렬
                .collect(Collectors.toList()); // 리스트로 반환
    }
}
