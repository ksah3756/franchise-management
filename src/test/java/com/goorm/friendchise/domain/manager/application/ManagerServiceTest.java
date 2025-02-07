package com.goorm.friendchise.domain.manager.application;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ManagerServiceTest {
	private ManagerService managerService;

	@BeforeEach
	void setUp() {
		ManagerRepository managerRepository = new FakeManagerRepository();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		TokenProvider tokenProvider = new TokenProvider(new JwtProperties());
		AuthService authService = new AuthService(managerRepository);
		managerService = new ManagerService(managerRepository, bCryptPasswordEncoder, tokenProvider, authService);

		managerRepository.save(
			Manager.create("test", "test1234", Role.HEADQUARTER)
		);
	}

	@Test
	void create_success() {
		ManageCreateRequest request = ManageCreateRequest.builder()
			.username("request")
			.password("test1234")
			.role(Role.HEADQUARTER)
			.build();
		ManagerPersistResponse response = managerService.create(request);
		assertNotNull(response);
		assertEquals(2L, response.id());
	}

	@Test
	void detail_success() {
		String inputName = "test";
		ManagerDetailResponse detail = managerService.detail(inputName);
		assertNotNull(detail);
		assertEquals(1L, detail.id());
		assertEquals(inputName, detail.username());
		assertEquals(Role.HEADQUARTER, detail.role());
		assertNull(detail.manageId());
	}

	@Test
	void findManagerByUsername_success() {
		String inputName = "test";
		Manager manager = managerService.findManagerByUsername(inputName);
		assertNotNull(managerService.findManagerByUsername(inputName));
		assertEquals(1L, manager.getId());
		assertEquals(inputName, manager.getUsername());
		assertEquals("test1234", manager.getPassword());
		assertEquals(Role.HEADQUARTER, manager.getRole());
		assertNull(manager.getManageId());
	}
}