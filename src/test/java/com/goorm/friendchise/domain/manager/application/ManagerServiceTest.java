package com.goorm.friendchise.domain.manager.application;

import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.infrastructure.FakeCustomerRepository;
import com.goorm.friendchise.domain.headquarter.domain.RestaurantCategory;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.RestaurantSubCategory;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.domain.manager.exception.HeadquarterAuthNotMatchException;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.infrastructure.FakeApplicationEventPublisher;
import com.goorm.friendchise.global.auth.implement.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.implement.jwt.TokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static com.goorm.friendchise.domain.manager.domain.Role.STORE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagerServiceTest {
	private ManagerService managerService;
	private HeadquarterRepository headquarterRepository;
	private Manager manager;

	@BeforeEach
	void setUp() {
		ManagerRepository managerRepository = new FakeManagerRepository();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		TokenProvider tokenProvider = new TokenProvider(new JwtProperties());
		CustomerRepository customerRepository = new FakeCustomerRepository();
		this.headquarterRepository = new FakeHeadquarterRepository();
		AuthService authService = new AuthService(tokenProvider, customerRepository, new FakeApplicationEventPublisher()); ;
		managerService = new ManagerService(managerRepository, bCryptPasswordEncoder,
			authService, headquarterRepository);

		Manager savedManager = managerRepository.save(
			Manager.create("test", "test1234", HEADQUARTER)
		);

		Headquarter headquarter = headquarterRepository.save(
			Headquarter.of("Mcdonald", RestaurantCategory.FASTFOOD, RestaurantSubCategory.NONE)
		);

		savedManager.updateManageId(headquarter.getId());

		manager = managerService.findManagerByUsername("test");
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
			new UsernamePasswordAuthenticationToken(manager, manager.getUsername(), manager.getAuthorities())
		);
	}

	@Test
	@DisplayName("create는 새로운 Manager를 생성")
	void create_success() {
		// given
		ManageCreateRequest request = ManageCreateRequest.builder()
			.username("request")
			.password("test1234")
			.role(HEADQUARTER)
			.build();

		// when
		ManagerPersistResponse response = managerService.create(request);

		// then
		assertNotNull(response);
		assertEquals(2L, response.id());
	}

	@Test
	@DisplayName("create는 store의 Headquarter의 certificationNumber가 일치하면 예외를 발생시키지 않음")
	void validateHeadquarterId_success() {
		// given
		Headquarter mcdonald = Headquarter.builder()
			.id(1L)
			.franchiseName("Mcdonald")
			.restaurantCategory(RestaurantCategory.FASTFOOD)
			.restaurantSubCategory(RestaurantSubCategory.NONE)
			.certificationNumber(UUID.randomUUID().toString())
			.build();

		Headquarter saved = headquarterRepository.save(mcdonald);

		ManageCreateRequest request = ManageCreateRequest.builder()
			.username("request")
			.password("test1234")
			.role(STORE)
			.headquarterId(saved.getId())
			.certificationNumber(saved.getCertificationNumber())
			.build();

		// when
		ManagerPersistResponse response = managerService.create(request);

		// then
		assertNotNull(response);
		assertEquals(2L, response.id());
	}

	@Test
	@DisplayName("create는 store의 Headquarter의 certificationNumber가 일치하지 않으면 예외를 발생")
	void validateHeadquarterId_HeadquarterAuthNotMatchException() {
		// given
		Headquarter mcdonald = Headquarter.builder()
			.id(1L)
			.franchiseName("Mcdonald")
			.restaurantCategory(RestaurantCategory.FASTFOOD)
			.restaurantSubCategory(RestaurantSubCategory.NONE)
			.certificationNumber(UUID.randomUUID().toString())
			.build();

		Headquarter saved = headquarterRepository.save(mcdonald);

		ManageCreateRequest request = ManageCreateRequest.builder()
			.username("request")
			.password("test1234")
			.role(STORE)
			.headquarterId(saved.getId())
			.certificationNumber("NOT_MATCH")
			.build();

		// then
		Assertions.assertThrows(
			HeadquarterAuthNotMatchException.class,
			() -> managerService.create(request)
		);
	}

	@Test
	@DisplayName("mypage는 SecurityContextHolder의 정보로 ManagerDetailResponse를 반환")
	void mypage_success() {
		// when
		ManagerDetailResponse mypage = managerService.mypage(manager);

		// then
		assertNotNull(mypage);
		assertEquals(1L, mypage.id());
		assertEquals("test", mypage.username());
		assertEquals(HEADQUARTER, mypage.role());
		assertEquals(1L, mypage.manageId());
		assertNotNull(mypage.certificationNumber());
	}

	@Test
	@DisplayName("updateManager는 Manager의 manageId를 변경")
	void updateManager_success() {
		// given
		Long newStoreId = 1L;

		// when
		managerService.updateManager(manager, newStoreId);
		Manager manager = managerService.findManagerByUsername("test");

		// then
		assertEquals(1L, manager.getId());
	}

	@Test
	@DisplayName("updatePassword는 Manager의 password를 변경")
	void updatePassword_success() {
		// given
		Manager manager = managerService.findManagerByUsername("test");
		String oldPassword = manager.getPassword();
		String newPassword = "newPassword";

		// when
		managerService.updatePassword(manager, newPassword);

		// then
		assertNotEquals(oldPassword, manager.getPassword());
	}

	@Test
	@DisplayName("delete는 Manager를 삭제")
	void delete_success() {
		// when
		managerService.delete(manager);

		// then
		Assertions.assertThrows(
			ManagerNotFoundException.class,
			() -> managerService.findManagerByUsername("test")
		);
	}

	@Test
	@DisplayName("findManagerByUsername은 username으로 Manager를 반환")
	void findManagerByUsername_success() {
		// given
		String inputName = "test";

		// when
		Manager manager = managerService.findManagerByUsername(inputName);

		// then
		assertNotNull(managerService.findManagerByUsername(inputName));
		assertEquals(1L, manager.getId());
		assertEquals(inputName, manager.getUsername());
		assertEquals("test1234", manager.getPassword());
		assertEquals(HEADQUARTER, manager.getRole());
		assertEquals(1L, manager.getManageId());
	}

	@Test
	@DisplayName("findManagerByUsername은 존재하지 않는 username으로 조회시 ManagerNotFoundException")
	void findManagerByUsername_ManagerNotFoundException() {
		// given
		String inputName = "notExist";

		// then
		Assertions.assertThrows(
			ManagerNotFoundException.class,
			() -> managerService.findManagerByUsername(inputName)
		);
	}
}