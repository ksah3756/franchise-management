package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.infrastructure.FakeCustomerRepository;
import com.goorm.friendchise.domain.headquarter.domain.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterDetailResDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.infrastructure.FakeRefreshTokenRepository;
import com.goorm.friendchise.global.auth.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class HeadquarterServiceTest {

	@Mock
	private StoreRepository storeRepository;

	private HeadquarterService headquarterService;
	private HeadquarterRepository headquarterRepository;

	@InjectMocks
	private AuthService authService;
	private ManagerRepository managerRepository;

	@BeforeEach
	void setup() {
		headquarterRepository = new FakeHeadquarterRepository();
		managerRepository = new FakeManagerRepository();
		CustomerRepository customerRepository = new FakeCustomerRepository();
		TokenProvider tokenProvider = new TokenProvider(new JwtProperties());
		RefreshTokenRepository refreshTokenRepository = new FakeRefreshTokenRepository();
		authService = new AuthService(managerRepository, tokenProvider, refreshTokenRepository, headquarterRepository, customerRepository, storeRepository);
		headquarterService = new HeadquarterService(authService, headquarterRepository);
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		headquarterRepository.deleteAll();
	}

	private void setContextHolder(Manager manager) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(manager, manager.getUsername(), manager.getAuthorities())
		);
	}

	@Test
	@DisplayName("성공적으로 본사에 속한 매장의 ID 목록을 조회한다.")
	void getStoreIdList_Success() {
		// given
		Manager manager = Manager.create("test", "test1234", HEADQUARTER);
		Manager savedManager = managerRepository.save(manager);
		setContextHolder(savedManager);

		Headquarter headquarter = Headquarter.builder()
				.franchiseName("test")
				.category(Category.FASTFOOD)
				.subCategory(SubCategory.NONE)
				.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		savedManager.updateManageId(savedHeadquarter.getId());

		StoreReqDto storeRegisterDto1 = StoreReqDto.builder()
				.address("서울시 강남구")
				.roadAddress("서울시 강남구 삼성동")
				.zoneNumber("04930")
				.dong("삼성동")
				.x(127.123)
				.y(37.321)
				.franchiseName("맥도날드 삼성점")
				.headQuarterName("맥도날드")
				.build();


		StoreReqDto storeRegisterDto2 = StoreReqDto.builder()
				.address("서울시 중곡동 140-6")
				.roadAddress("서울시 광진구 천호대로116 9")
				.dong("중곡동")
				.x(127.456)
				.y(37.654)
				.franchiseName("맥도날드 중곡점")
				.headQuarterName("맥도날드")
				.build();

		Store store1 = new Store(storeRegisterDto1, savedHeadquarter, savedManager);
		Store store2 = new Store(storeRegisterDto2, savedHeadquarter, savedManager);

		// when
		List<StoreIdDto> storeIds = headquarterService.getStoreIdList(savedHeadquarter.getId());

		// then
		assertThat(storeIds).hasSize(2);
		assertThat(storeIds).extracting(StoreIdDto::id)
				.containsExactlyInAnyOrder(store1.getId(), store2.getId());

	}

	@Test
	@DisplayName("존재하지 않는 본사의 매장 ID 목록을 조회하면 예외를 던진다.")
	void getStoreIdList_headquarterNotFound() {
		// given
		createManager();
		Long nonExistentId = 999L;

		// when, then
		assertThatThrownBy(() -> headquarterService.getStoreIdList(nonExistentId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
	}

	@Test
	@DisplayName("성공적으로 본사를 생성한다.")
	void createHeadquarter() {
		// given
		createManager();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");

		// when
		 HeadquarterResDto res = headquarterService.createHeadquarter(headquarterReqDto);

		// then
		assertThat(res.franchiseName()).isEqualTo("test");
	}

	private void createManager() {
		Manager manager = Manager.create("test", "test1234", HEADQUARTER);
		managerRepository.save(manager);
		setContextHolder(manager);
	}

	@Test
	@DisplayName("동일한 프랜차이즈 이름의 본사가 이미 있을 경우 예외를 던진다.")
	void createHeadquarter_duplicateFranchiseName() {
		// given
		createManager();
		Headquarter headquarter = Headquarter.builder()
			.franchiseName("test")
			.category(Category.FASTFOOD)
			.subCategory(SubCategory.NONE)
			.build();
		headquarterRepository.save(headquarter);

		// when, then
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");
		assertThatThrownBy(() -> headquarterService.createHeadquarter(headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_NAME_DUPLICATION);
	}

	@Test
	@DisplayName("카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
	void createHeadquarter_noCategory() {
		// given
		createManager();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "", "");

		// when, then
		assertThatThrownBy(() -> headquarterService.createHeadquarter(headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("서브 카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
	void createHeadquarter_noSubCategory() {
		// given
		createManager();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "dsadsa");

		// when, then
		assertThatThrownBy(() -> headquarterService.createHeadquarter(headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_SUBCATEGORY_NOT_FOUND);
	}


	@Test
	@DisplayName("성공적으로 본사를 조회한다.")
	void getHeadquarter() {
		// given
		createManagerAndHeadquarter();

		// when
		HeadquarterDetailResDto headquarterResDto = headquarterService.getHeadquarter();

		// then
		assertThat(headquarterResDto.franchiseName()).isEqualTo("test");
	}

	private Long createManagerAndHeadquarter() {
		Manager manager = Manager.create("test", "test1234", HEADQUARTER);
		Manager savedManager = managerRepository.save(manager);
		setContextHolder(savedManager);

		Headquarter headquarter = Headquarter.builder()
				.franchiseName("test")
				.category(Category.FASTFOOD)
				.subCategory(SubCategory.NONE)
			.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		savedManager.updateManageId(savedHeadquarter.getId());

		return savedHeadquarter.getId();
	}

	@Test
	@DisplayName("존재하지 않는 본사를 조회할 경우 예외를 던진다.")
	void getHeadquarter_notFound() {
		// given
		createManager();

		// when, then
		assertThatThrownBy(() -> headquarterService.getHeadquarter())
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
	}

	@Test
	@DisplayName("성공적으로 프랜차이즈 정보를 수정한다.")
	void updateHeadquarterName() {
		// given
		createManagerAndHeadquarter();

		// when
		HeadquarterResDto res = headquarterService.updateHeadquarterName(HeadquarterReqDto.of("newTest", "한식", "국밥"));

		// then
		Headquarter headquarter = headquarterRepository.findById(res.id()).orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
		assertThat(headquarter.getFranchiseName()).isEqualTo("newTest");
		assertThat(headquarter.getCategory()).isEqualTo(Category.KOREANFOOD);
		assertThat(headquarter.getSubCategory()).isEqualTo(SubCategory.GOOKBAB);
	}

	@Test
	@DisplayName("성공적으로 본사를 삭제한다.")
	void deleteHeadquarter() {
		// given
		Long headquarterId = createManagerAndHeadquarter();

		// when
		headquarterService.deleteHeadquarter();

		// then
		assertThat(headquarterRepository.findById(headquarterId).isEmpty()).isTrue();
	}
}