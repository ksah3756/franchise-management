package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.exception.CustomerException;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.event.RefreshTokenSaveEvent;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final TokenService tokenService;
	private final CustomerRepository customerRepository;
	private final ApplicationEventPublisher eventPublisher;

	public Customer findCustomerByAuth() {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((UserDetails) principal).getUsername();
			return customerRepository.findByUsername(username)
				.orElseThrow(ManagerNotFoundException::new);
		} catch (Exception e) {
			throw new CustomerException(ErrorCode.USER_NOT_FOUND);
		}
	}

	public TokenResponse managerLogin(Manager manager) {
		String username = manager.getUsername();
		String role = manager.getRole().name();

		String accessToken = tokenService.generateAccessToken(username, role);
		String refreshToken = tokenService.generateRefreshToken(username, role);

		// 이 부분을 이벤트로 발행해서 비동기로?
		eventPublisher.publishEvent(RefreshTokenSaveEvent.create(refreshToken, manager.getId(), manager.getRole()));
		return TokenResponse.of(accessToken, refreshToken);
	}

	public TokenResponse customerLogin(Customer customer) {
		String username = customer.getUsername();

		String accessToken = tokenService.generateAccessToken(username, Role.USER.getDescription());
		String refreshToken = tokenService.generateRefreshToken(username, Role.USER.getDescription());

		eventPublisher.publishEvent(RefreshTokenSaveEvent.create(refreshToken, customer.getId(), Role.USER));
		return TokenResponse.of(accessToken, refreshToken);
	}

}
