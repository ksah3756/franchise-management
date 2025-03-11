package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
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
import com.goorm.friendchise.global.event.ManagerUpdateEvent;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeadquarterServiceTest {

	private HeadquarterService headquarterService;
	private HeadquarterRepository headquarterRepository;

	private ManagerRepository managerRepository;

	@BeforeEach
	void setup() {
		headquarterRepository = new FakeHeadquarterRepository();
		managerRepository = new FakeManagerRepository();

		headquarterService = new HeadquarterService(headquarterRepository, event -> {
			ManagerUpdateEvent managerUpdateEvent = (ManagerUpdateEvent) event;
			Manager manager = managerUpdateEvent.getManager();
			manager.updateManageId(managerUpdateEvent.getManageId());
			managerRepository.save(manager);
			System.out.println("manageId " + managerUpdateEvent.getManageId() + "가 연결되었습니다. Role: " + manager.getRole().getDescription());
		});
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
		createManagerWithoutManageId();
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
		Manager manager = createManagerWithoutManageId();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");

		// when
		 HeadquarterResDto res = headquarterService.createHeadquarter(manager, headquarterReqDto);

		// then
		assertThat(res.franchiseName()).isEqualTo("test");
		assertThat(managerRepository.findById(manager.getId()).get().getManageId()).isEqualTo(res.id());
	}

	private Manager createManagerWithoutManageId() {
		return Manager.builder()
				.id(1L)
				.username("test")
				.password("test1234")
				.role(HEADQUARTER)
				.build();
	}

	private Manager createManager(Long headquarterId) {
		return Manager.builder()
				.id(1L)
				.username("test")
				.password("test1234")
				.role(HEADQUARTER)
				.manageId(headquarterId)
				.build();
	}

	@Test
	@DisplayName("동일한 프랜차이즈 이름의 본사가 이미 있을 경우 예외를 던진다.")
	void createHeadquarter_duplicateFranchiseName() {
		// given
		Headquarter headquarter = Headquarter.builder()
			.franchiseName("test")
			.category(Category.FASTFOOD)
			.subCategory(SubCategory.NONE)
			.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		Manager manager = createManager(savedHeadquarter.getId());

		// when, then
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");
		assertThatThrownBy(() -> headquarterService.createHeadquarter(manager, headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_NAME_DUPLICATION);
	}

	@Test
	@DisplayName("카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
	void createHeadquarter_noCategory() {
		// given
		Manager manager = createManagerWithoutManageId();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "", "");

		// when, then
		assertThatThrownBy(() -> headquarterService.createHeadquarter(manager, headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("서브 카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
	void createHeadquarter_noSubCategory() {
		// given
		Manager manager = createManagerWithoutManageId();
		HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "dsadsa");

		// when, then
		assertThatThrownBy(() -> headquarterService.createHeadquarter(manager, headquarterReqDto))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_SUBCATEGORY_NOT_FOUND);
	}


	@Test
	@DisplayName("성공적으로 본사를 조회한다.")
	void getHeadquarter() {
		// given
		Headquarter headquarter = Headquarter.builder()
				.franchiseName("test")
				.category(Category.FASTFOOD)
				.subCategory(SubCategory.NONE)
				.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		Manager manager = createManager(savedHeadquarter.getId());

		// when
		HeadquarterDetailResDto headquarterResDto = headquarterService.getHeadquarter(manager);

		// then
		assertThat(headquarterResDto.franchiseName()).isEqualTo("test");
	}

	@Test
	@DisplayName("존재하지 않는 본사를 조회할 경우 예외를 던진다.")
	void getHeadquarter_notFound() {
		// given
		Manager manager = createManagerWithoutManageId();

		// when, then
		assertThatThrownBy(() -> headquarterService.getHeadquarter(manager))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
	}

	@Test
	@DisplayName("성공적으로 프랜차이즈 정보를 수정한다.")
	void updateHeadquarterName() {
		// given
		Headquarter headquarter = Headquarter.builder()
				.franchiseName("test")
				.category(Category.FASTFOOD)
				.subCategory(SubCategory.NONE)
				.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		Manager manager = createManager(savedHeadquarter.getId());

		// when
		HeadquarterResDto res = headquarterService.updateHeadquarterName(manager, HeadquarterReqDto.of("newTest", "한식", "국밥"));

		// then
		Headquarter foundHeadquarter = headquarterRepository.findById(res.id()).orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
		assertThat(foundHeadquarter.getFranchiseName()).isEqualTo("newTest");
		assertThat(foundHeadquarter.getCategory()).isEqualTo(Category.KOREANFOOD);
		assertThat(foundHeadquarter.getSubCategory()).isEqualTo(SubCategory.GOOKBAB);
	}

	@Test
	@DisplayName("성공적으로 본사를 삭제한다.")
	void deleteHeadquarter() {
		// given
		Headquarter headquarter = Headquarter.builder()
				.franchiseName("test")
				.category(Category.FASTFOOD)
				.subCategory(SubCategory.NONE)
				.build();
		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
		Manager manager = createManager(savedHeadquarter.getId());

		// when
		headquarterService.deleteHeadquarter(manager);

		// then
		assertThat(headquarterRepository.findById(headquarter.getId()).isEmpty()).isTrue();
	}
}