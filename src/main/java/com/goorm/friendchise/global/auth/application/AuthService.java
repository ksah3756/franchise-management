package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.domain.manager.exception.TokenNotFoundException;
import com.goorm.friendchise.global.auth.domain.RefreshToken;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.dto.request.TokenReissueRequest;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.goorm.friendchise.global.exception.ErrorCode.HEADQUARTER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final ManagerRepository managerRepository;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final HeadquarterRepository headquarterRepository;

	private static final String HEADQUARTER_ROLE = "HEADQUARTER";
	private static final Duration REFRESH_TOKEN_EXP = Duration.ofDays(1);
	private static final Duration ACCESS_TOKEN_EXP = Duration.ofHours(1);

	public Manager findManagerByAuth() {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((UserDetails) principal).getUsername();
			return managerRepository.findByUsername(username)
				.orElseThrow(ManagerNotFoundException::new);
		} catch (Exception e) {
			throw new ManagerNotFoundException();
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

		refreshTokenRepository.save(
			RefreshToken.of(refreshToken, manager.getId(), manager.getRole())
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
