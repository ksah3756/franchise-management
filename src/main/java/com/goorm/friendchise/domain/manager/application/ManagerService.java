package com.goorm.friendchise.domain.manager.application;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManageLoginRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerTokenResponse;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Transactional
@Service
@RequiredArgsConstructor
public class ManagerService {
	private final ManagerRepository managerRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final TokenProvider tokenProvider;
	private final AuthService authService;

	private static final Duration REFRESH_TOKEN_EXP = Duration.ofDays(1);
	private static final Duration ACCESS_TOKEN_EXP = Duration.ofHours(1);

	public ManagerPersistResponse create(ManageCreateRequest request) {
		String encodedPassword = bCryptPasswordEncoder.encode(request.password());
		Manager manager = Manager.create(request.username(), encodedPassword, request.role());
		Long id = managerRepository.save(manager).getId();
		return ManagerPersistResponse.of(id);
	}

	public ManagerTokenResponse login(ManageLoginRequest request) {
		String name = request.username();
		Manager manager = findManagerByUsername(name);
		manager.isPasswordMatch(request.password(), bCryptPasswordEncoder);

		String role = manager.getRole().name();
		String refreshToken = tokenProvider.generateToken(name, REFRESH_TOKEN_EXP, role);
		String accessToken = tokenProvider.generateToken(name, ACCESS_TOKEN_EXP, role);

		return ManagerTokenResponse.of(refreshToken, accessToken);
	}

	public ManagerDetailResponse detail(String username) {
		Manager manager = findManagerByUsername(username);
		return ManagerDetailResponse.from(manager);
	}

	public ManagerDetailResponse mypage() {
		Manager manager = authService.findManagerByAuth();
		return ManagerDetailResponse.from(manager);
	}

	public void updateManager(Long newStoreId) {
		Manager manager = authService.findManagerByAuth();
		manager.updateManageId(newStoreId);
	}

	public void updatePassword(String newPassword) {
		String encode = bCryptPasswordEncoder.encode(newPassword);
		Manager manager = authService.findManagerByAuth();
		manager.updatePassword(encode);
	}

	public void delete() {
		Manager manager = authService.findManagerByAuth();
		managerRepository.delete(manager);
	}

	public Manager findManagerByUsername(String username) {
		return managerRepository.findByUsername(username)
			.orElseThrow(ManagerNotFoundException::new);
	}
}
