package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.exception.CustomerException;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.domain.manager.exception.TokenNotFoundException;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.domain.RefreshToken;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.dto.request.TokenReissueRequest;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.goorm.friendchise.global.exception.ErrorCode.HEADQUARTER_NOT_FOUND;
import static com.goorm.friendchise.global.exception.ErrorCode.STORE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final ManagerRepository managerRepository;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final HeadquarterRepository headquarterRepository;
	private final CustomerRepository customerRepository;
	private final StoreRepository storeRepository;

	private static final String HEADQUARTER_ROLE = "HEADQUARTER";
	private static final String STORE_ROLE = "STORE";
	private static final Duration REFRESH_TOKEN_EXP = Duration.ofDays(1);
	private static final Duration ACCESS_TOKEN_EXP = Duration.ofHours(1);

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
		String name = manager.getUsername();
		String role = manager.getRole().name();
		String accessToken = tokenProvider.generateToken(name, ACCESS_TOKEN_EXP, role);
		String refreshToken = tokenProvider.generateToken(name, REFRESH_TOKEN_EXP, role);

		if (role.equals(HEADQUARTER_ROLE) && manager.getManageId() != null) {
			accessToken = headquarterAccessToken(manager, role, name);
		}

		if (role.equals(STORE_ROLE) && manager.getManageId() != null) {
			accessToken = storeAccessToken(manager, role, name);
		}

		refreshTokenRepository.save(
			RefreshToken.of(refreshToken, manager.getId(), manager.getRole())
		);

		return TokenResponse.of(accessToken, refreshToken);
	}

	public TokenResponse customerLogin(Customer customer) {
		String name = customer.getUsername();

		String accessToken = tokenProvider.generateToken(name, ACCESS_TOKEN_EXP, "USER");
		String refreshToken = tokenProvider.generateToken(name, REFRESH_TOKEN_EXP, "USER");

		refreshTokenRepository.save(
			RefreshToken.of(refreshToken, customer.getId(), Role.USER)
		);

		return TokenResponse.of(accessToken, refreshToken);
	}

	private String headquarterAccessToken(Manager manager, String role, String name) {
		Long manageId = manager.getManageId();
		Headquarter headquarter = headquarterRepository.findById(manageId)
			.orElseThrow(() -> new CustomException(HEADQUARTER_NOT_FOUND));

		return tokenProvider.generateToken(name, ACCESS_TOKEN_EXP, role, manager.getId(),
			headquarter.getCategory().getValue(), headquarter.getSubCategory().getValue());
	}

	private String storeAccessToken(Manager manager, String role, String name) {
		Long manageId = manager.getManageId();
		Store store = storeRepository.findById(manageId)
			.orElseThrow(() -> new CustomException(STORE_NOT_FOUND));

		return tokenProvider.generateToken(name, ACCESS_TOKEN_EXP, role, store.getId());
	}

	public TokenResponse reissue(TokenReissueRequest request) {
		String inputRefreshToken = request.refreshToken();

		String username = tokenProvider.getUsername(inputRefreshToken);

		RefreshToken savedRefreshToken = refreshTokenRepository.findByRefreshToken(inputRefreshToken)
			.orElseThrow(TokenNotFoundException::new);

		Role role = savedRefreshToken.getRole();
		String roleName = role.name();

		String accessToken = tokenProvider.generateToken(username, ACCESS_TOKEN_EXP, roleName);
		String refreshToken = tokenProvider.generateToken(username, REFRESH_TOKEN_EXP, roleName);

		// reissue에도 HQ manager면 정보 추가
		if (roleName.equals(HEADQUARTER_ROLE)) {
			Manager manager = managerRepository.findByUsername(username)
				.orElseThrow(ManagerNotFoundException::new);
			accessToken = headquarterAccessToken(manager, roleName, username);
		}

		refreshTokenRepository.save(
			RefreshToken.of(refreshToken, savedRefreshToken.getId(), role)
		);

		return TokenResponse.of(accessToken, refreshToken);
	}
}
