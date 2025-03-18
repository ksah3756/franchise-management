package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.infrastructure.FakeCustomerRepository;
import com.goorm.friendchise.domain.customer.infrastructure.FakeStoreRepository;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.infrastructure.FakeApplicationEventPublisher;
import com.goorm.friendchise.global.auth.infrastructure.FakeRefreshTokenRepository;
import com.goorm.friendchise.global.auth.implement.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.implement.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthServiceTest {
	private AuthService authService;

	@BeforeEach
	void setUp() {
		TokenProvider tokenProvider = new TokenProvider(new JwtProperties());
		FakeManagerRepository managerRepository = new FakeManagerRepository();
		CustomerRepository customerRepository = new FakeCustomerRepository();
		this.authService = new AuthService(tokenProvider, customerRepository, new FakeApplicationEventPublisher());

		managerRepository.save(
			Manager.create("test", "test1234", HEADQUARTER)
		);

		UserDetails manger = managerRepository.findByUsername("test").get();
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
			new UsernamePasswordAuthenticationToken(manger, manger.getUsername(), manger.getAuthorities())
		);
	}
}