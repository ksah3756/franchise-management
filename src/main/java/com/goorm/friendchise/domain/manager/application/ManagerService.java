package com.goorm.friendchise.domain.manager.application;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManageLoginRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static com.goorm.friendchise.domain.manager.domain.Role.STORE;
import static com.goorm.friendchise.global.exception.ErrorCode.HEADQUARTER_NOT_FOUND;
import static com.goorm.friendchise.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {
	private final ManagerRepository managerRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AuthService authService;
	private final HeadquarterRepository headquarterRepository;

	@Transactional
	public ManagerPersistResponse create(ManageCreateRequest request) {
		// STORE일 경우 HQ의 certificationNumber 비교
		if (request.role().equals(STORE)) {
			Long headquarterId = request.headquarterId();
			if (headquarterId == null)
				throw new CustomException(INVALID_PARAMETER);

			Headquarter hq = headquarterRepository.findById(headquarterId)
				.orElseThrow(() -> new CustomException(HEADQUARTER_NOT_FOUND));

			hq.validateCertificationNumber(request.certificationNumber());
		}

		String encodedPassword = bCryptPasswordEncoder.encode(request.password());
		Manager manager = Manager.create(request.username(), encodedPassword, request.role());
		Long id = managerRepository.save(manager).getId();
		return ManagerPersistResponse.of(id);
	}

	public TokenResponse login(ManageLoginRequest request) {
		String name = request.username();
		Manager manager = findManagerByUsername(name);
		manager.isPasswordMatch(request.password(), bCryptPasswordEncoder);
		return authService.managerLogin(manager);
	}

	public ManagerDetailResponse mypage(Manager manager) {

		if (manager.getRole().equals(HEADQUARTER)) {
			Headquarter headquarter = headquarterRepository.findById(manager.getManageId())
				.orElseThrow(() -> new CustomException(HEADQUARTER_NOT_FOUND));
			return ManagerDetailResponse.fromHeadquarter(manager, headquarter.getCertificationNumber());
		}
		return ManagerDetailResponse.from(manager);
	}

	@Transactional
	public void updateManager(Manager manager, Long newStoreId) {
		manager.updateManageId(newStoreId);
	}

	@Transactional
	public void updatePassword(Manager manager, String newPassword) {
		String encode = bCryptPasswordEncoder.encode(newPassword);
		manager.updatePassword(encode);
	}

	@Transactional
	public void delete(Manager manager) {
		managerRepository.delete(manager);
	}

	public Manager findManagerByUsername(String username) {
		return managerRepository.findByUsername(username)
			.orElseThrow(ManagerNotFoundException::new);
	}
}
