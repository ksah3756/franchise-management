//package com.goorm.api.headquarter.application;
//
//import com.goorm.api.headquarter.dto.headquarter.HeadquarterCreateRequest;
//import com.goorm.api.headquarter.dto.headquarter.HeadquarterDetailResponse;
//import com.goorm.api.headquarter.dto.headquarter.HeadquarterResponse;
//import com.goorm.api.headquarter.dto.headquarter.HeadquarterUpdateRequest;
//import com.goorm.api.headquarter.dto.store.StoreIdDto;
//import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
//import com.goorm.api.headquarter.implement.headquarter.HeadquarterValidator;
//import com.goorm.api.headquarter.insfrastructure.FakeHeadquarterRepository;
//import com.goorm.api.store.dto.StoreReqDto;
//import com.goorm.core.common.exception.CustomException;
//import com.goorm.core.common.exception.ErrorCode;
//
//import com.goorm.core.headquarter.domain.Headquarter;
//import com.goorm.core.headquarter.domain.HeadquarterRepository;
//import com.goorm.core.headquarter.domain.RestaurantCategory;
//import com.goorm.core.headquarter.domain.RestaurantSubCategory;
//import com.goorm.core.store.domain.Store;
//import com.goorm.core.user.domain.User;
//import com.goorm.core.user.domain.UserRole;
//import org.apache.catalina.Manager;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class HeadquarterServiceTest {
//
//	@Autowired
//	private HeadquarterRepository headquarterRepository;
//
//	@Autowired
//	private HeadquarterService headquarterService;
//
//	private User createUser() {
//		return User.builder()
//				.id(1L)
//				.username("test")
//				.password("test1234")
//				.userRole(UserRole.HEADQUARTER)
//				.build();
//	}
//
//	@Test
//	@DisplayName("성공적으로 본사에 속한 매장의 ID 목록을 조회한다.")
//	void getStoreIdList_Success() {
//		// given
//		User user = createUser();
//
//		Headquarter headquarter = Headquarter.builder()
//				.id(user.getId())
//				.franchiseName("test")
//				.restaurantCategory(RestaurantCategory.FASTFOOD)
//				.restaurantSubCategory(RestaurantSubCategory.NONE)
//				.certificationNumber("123456")
//				.build();
//
//		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//
//		StoreReqDto storeRegisterDto1 = StoreReqDto.builder()
//				.name("맥도날드 삼성점")
//				.address("서울시 강남구")
//				.roadAddress("서울시 강남구 삼성동")
//				.zoneNumber("04930")
//				.dong("삼성동")
//				.x(127.123)
//				.y(37.321)
//				.franchiseName("맥도날드")
//				.certificationNumber("123456")
//				.build();
//
//
//		StoreReqDto storeRegisterDto2 = StoreReqDto.builder()
//				.name("맥도날드 중곡점")
//				.address("서울시 중곡동 140-6")
//				.roadAddress("서울시 광진구 천호대로116 9")
//				.dong("중곡동")
//				.x(127.456)
//				.y(37.654)
//				.franchiseName("맥도날드")
//				.certificationNumber("123456")
//				.build();
//
//		Store store1 = Store.create(
//				storeRegisterDto1.name(),
//				storeRegisterDto1.address(),
//				storeRegisterDto1.dong(),
//				storeRegisterDto1.x(),
//				storeRegisterDto1.y(),
//				storeRegisterDto1.franchiseName(),
//				savedHeadquarter.getId(),
//				user
//		);
//		Store store2 = Store.create(
//				storeRegisterDto2.name(),
//				storeRegisterDto2.address(),
//				storeRegisterDto2.dong(),
//				storeRegisterDto2.x(),
//				storeRegisterDto2.y(),
//				storeRegisterDto2.franchiseName(),
//				savedHeadquarter.getId(),
//				user
//		);
//
//		// when
//		List<StoreIdDto> storeIds = headquarterService.getStoreIdList(savedHeadquarter.getId());
//
//		// then
//		assertThat(storeIds).hasSize(2);
//		assertThat(storeIds).extracting(StoreIdDto::id)
//				.containsExactlyInAnyOrder(store1.getId(), store2.getId());
//
//	}
//
//	@Test
//	@DisplayName("존재하지 않는 본사의 매장 ID 목록을 조회하면 예외를 던진다.")
//	void getStoreIdList_headquarterNotFound() {
//		// given
//		Long nonExistentId = 999L;
//
//		// when, then
//		assertThatThrownBy(() -> headquarterService.getStoreIdList(nonExistentId))
//			.isInstanceOf(CustomException.class)
//			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
//	}
//
//	@Test
//	@DisplayName("성공적으로 본사를 생성한다.")
//	void createHeadquarter() {
//		// given
//		User user = User.builder()
//				.id(1L)
//				.username("test")
//				.password("test1234")
//				.userRole(UserRole.HEADQUARTER)
//				.build();
//
//		HeadquarterCreateRequest headquarterRequest = HeadquarterCreateRequest.of("test", "패스트푸드", "");
//
//		// when
//		 HeadquarterResponse res = headquarterService.createHeadquarter(user, headquarterRequest);
//
//		// then
//		assertThat(res.franchiseName()).isEqualTo("test");
//	}
//
//	@Test
//	@DisplayName("동일한 프랜차이즈 이름의 본사가 이미 있을 경우 예외를 던진다.")
//	void createHeadquarter_duplicateFranchiseName() {
//		// given
//		Headquarter headquarter = Headquarter.builder()
//			.franchiseName("test")
//			.restaurantCategory(RestaurantCategory.FASTFOOD)
//			.restaurantSubCategory(RestaurantSubCategory.NONE)
//			.build();
//		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//
//		User user = User.builder()
//			.id(savedHeadquarter.getId())
//			.username("test")
//			.password("test1234")
//			.userRole(UserRole.HEADQUARTER)
//			.build();
//
//		// when, then
//		HeadquarterCreateRequest headquarterRequest = HeadquarterCreateRequest.of("test", "패스트푸드", "");
//		assertThatThrownBy(() -> headquarterService.createHeadquarter(user, headquarterRequest))
//			.isInstanceOf(CustomException.class)
//			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_NAME_DUPLICATION);
//	}
//
//	@Test
//	@DisplayName("카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
//	void createHeadquarter_noCategory() {
//		// given
//		User user = User.builder()
//				.id(1L)
//				.username("test")
//				.password("test1234")
//				.userRole(UserRole.HEADQUARTER)
//				.build();
//		HeadquarterCreateRequest headquarterRequest = HeadquarterCreateRequest.of("test", "", "");
//
//		// when, then
//		assertThatThrownBy(() -> headquarterService.createHeadquarter(user, headquarterRequest))
//			.isInstanceOf(CustomException.class)
//			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND);
//	}
//
//	@Test
//	@DisplayName("서브 카테고리 정보가 정의되어 있지 않을 경우 예외를 던진다.")
//	void createHeadquarter_noSubCategory() {
//		// given
//		User user = createUser();
//		HeadquarterCreateRequest headquarterRequest = HeadquarterCreateRequest.of("test", "패스트푸드", "dsadsa");
//
//		// when, then
//		assertThatThrownBy(() -> headquarterService.createHeadquarter(user, headquarterRequest))
//			.isInstanceOf(CustomException.class)
//			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_SUBCATEGORY_NOT_FOUND);
//	}
//
//
//	@Test
//	@DisplayName("성공적으로 본사를 조회한다.")
//	void getHeadquarter() {
//		// given
//		Headquarter headquarter = Headquarter.builder()
//				.franchiseName("test")
//				.restaurantCategory(RestaurantCategory.FASTFOOD)
//				.restaurantSubCategory(RestaurantSubCategory.NONE)
//				.build();
//		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//		User user = createUser();
//
//		// when
//		HeadquarterDetailResponse headquarterResDto = headquarterService.getHeadquarter(user);
//
//		// then
//		assertThat(headquarterResDto.franchiseName()).isEqualTo("test");
//	}
//
//	@Test
//	@DisplayName("존재하지 않는 본사를 조회할 경우 예외를 던진다.")
//	void getHeadquarter_notFound() {
//		// given
//		User user = createUser();
//
//		// when, then
//		assertThatThrownBy(() -> headquarterService.getHeadquarter(user))
//			.isInstanceOf(CustomException.class)
//			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
//	}
//
//	@Test
//	@DisplayName("성공적으로 프랜차이즈 정보를 수정한다.")
//	void updateHeadquarterName() {
//		// given
//		Headquarter headquarter = Headquarter.builder()
//				.franchiseName("test")
//				.restaurantCategory(RestaurantCategory.FASTFOOD)
//				.restaurantSubCategory(RestaurantSubCategory.NONE)
//				.build();
//		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//		User user = createUser();
//
//		// when
//		HeadquarterResponse res = headquarterService.updateHeadquarter(user, HeadquarterUpdateRequest.of("newTest", "한식", "국밥"));
//
//		// then
//		Headquarter foundHeadquarter = headquarterRepository.findById(res.id()).orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
//		assertThat(foundHeadquarter.getFranchiseName()).isEqualTo("newTest");
//		assertThat(foundHeadquarter.getRestaurantCategory()).isEqualTo(RestaurantCategory.KOREANFOOD);
//		assertThat(foundHeadquarter.getRestaurantSubCategory()).isEqualTo(RestaurantSubCategory.GOOKBAB);
//	}
//
//	@Test
//	@DisplayName("성공적으로 본사를 삭제한다.")
//	void deleteHeadquarter() {
//		// given
//		Headquarter headquarter = Headquarter.builder()
//				.franchiseName("test")
//				.restaurantCategory(RestaurantCategory.FASTFOOD)
//				.restaurantSubCategory(RestaurantSubCategory.NONE)
//				.build();
//		Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//		User user = createUser();
//
//		// when
//		headquarterService.deleteHeadquarter(user);
//
//		// then
//		assertThat(headquarterRepository.findById(headquarter.getId()).isEmpty()).isTrue();
//	}
//}