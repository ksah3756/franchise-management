package com.goorm.friendchise.domain.location.application;

import com.goorm.friendchise.domain.customer.application.CustomerDistanceService;
import com.goorm.friendchise.domain.customer.application.CustomerService;
import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.dto.request.CustomerCreateRequest;
import com.goorm.friendchise.domain.customer.dto.request.CustomerDestinationRequest;
import com.goorm.friendchise.domain.customer.dto.request.CustomerStartLocationRequest;
import com.goorm.friendchise.domain.customer.dto.response.CustomerPersistResponse;
import com.goorm.friendchise.domain.customer.infrastructure.FakeCustomerRepository;
import com.goorm.friendchise.domain.customer.infrastructure.FakeStoreRepository;
import com.goorm.friendchise.domain.location.domain.Location;
import com.goorm.friendchise.domain.location.infrastructure.FakeLocationRepository;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.infrastructure.FakeRefreshTokenRepository;
import com.goorm.friendchise.global.auth.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LocationServiceTest {
    private LocationService locationService;


    private final FakeLocationRepository fakeLocationRepository = new FakeLocationRepository();
    @BeforeEach
    public void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        TokenProvider tokenProvider= new TokenProvider(jwtProperties);
        CustomerRepository customerRepository = new FakeCustomerRepository();
        RefreshTokenRepository refreshTokenRepository = new FakeRefreshTokenRepository();
        StoreRepository storeRepository = new FakeStoreRepository();
        AuthService authService = new AuthService(null, tokenProvider,
            refreshTokenRepository, null, customerRepository, storeRepository);

        CustomerService customerService =new CustomerService(customerRepository,
                null,null,null,null,null,null,null);
        CustomerDistanceService customerDistanceService=new CustomerDistanceService(customerService);
        locationService=new LocationService(authService,fakeLocationRepository,customerDistanceService);

        Customer customer=Customer.builder().id(1L).username("test").password("sddd").build();
        customerRepository.save(customer);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(customer, customer.getUsername(), customer.getAuthorities())
        );
    }

    @Test
    void 출발지점_도착지점_기록()
    {
        //로그인 상황
        CustomerStartLocationRequest request=
                new CustomerStartLocationRequest(15.555555,14.222222);
        locationService.saveStartLocation(request);
        Location location=fakeLocationRepository.findAll().get(0);
        assertEquals(14.222222, location.getStartX());
        assertEquals(15.555555, location.getStartY());
        System.out.println(location.getCustomer().getUsername());

        //로그아웃 상황
        CustomerDestinationRequest dRequest=
                new CustomerDestinationRequest(16.555555,15.222222);
        locationService.saveDestinationLocation(dRequest);
        Location dLocation=fakeLocationRepository.findAll().get(0);
        System.out.println(dLocation.getCustomer().getMovedDistance()+"km를 걸었습니다.");
        assertEquals(15.222222, dLocation.getDestinationX());
        assertEquals(16.555555, dLocation.getDestinationY());
    }


}
