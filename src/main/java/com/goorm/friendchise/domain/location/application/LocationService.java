package com.goorm.friendchise.domain.location.application;


import com.goorm.friendchise.domain.customer.application.CustomerDistanceService;
import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.dto.request.CustomerDestinationRequest;
import com.goorm.friendchise.domain.customer.dto.request.CustomerStartLocationRequest;
import com.goorm.friendchise.domain.customer.exception.CustomerException;
import com.goorm.friendchise.domain.location.domain.Location;
import com.goorm.friendchise.domain.location.domain.LocationRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Import({TokenProvider.class, JwtProperties.class})

public class LocationService
{
    private final AuthService authService;

    private final LocationRepository locationRepository;

    private final CustomerDistanceService customerDistanceService;
    @Transactional
    public void saveStartLocation(CustomerStartLocationRequest request)
    {
        Customer customer = authService.findCustomerByAuth();
        Location location = Location.builder()
                .customer(customer)
                .startY(request.startY())
                .startX(request.startX())
                .build();
        locationRepository.save(location);
    }

    @Transactional
    public void saveDestinationLocation(CustomerDestinationRequest request)
    {
        Customer customer = authService.findCustomerByAuth();
        List<Location> locationList= locationRepository.findByCustomerUsernameOrderByRecordedAtDesc(customer.getUsername());
        if(locationList.isEmpty())
            throw new CustomerException(ErrorCode.NOT_FOUND_ADDRESS);
        Location location =locationList.get(0);
        location.setDestination(request.destinationX(), request.destinationY());

        customerDistanceService.updateMovedDistance(location.getCustomer(),location);
    }
}
