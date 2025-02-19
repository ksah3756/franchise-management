package com.goorm.friendchise.domain.customer.application;

import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.location.domain.Location;
import com.goorm.friendchise.global.auth.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerDistanceService {
    private final CustomerRepository customerRepository;

    public void updateMovedDistance(Customer customer, Location location) {
        double distance = DistanceCalculator.calculateDistance(
                location.getStartY(), location.getStartX(),
                location.getDestinationY(), location.getDestinationX()
        );
        customer.plusMovedDistance(distance);
        customerRepository.save(customer);
    }
}