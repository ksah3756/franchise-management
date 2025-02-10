package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.domain.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeadquarterServiceTest {
    private HeadquarterService headquarterService;
    private HeadquarterRepository headquarterRepository;
    @BeforeEach
    void setup() {
        headquarterRepository = new FakeHeadquarterRepository();
        headquarterService = new HeadquarterService(headquarterRepository);
    }

    @Test
    @DisplayName("성공적으로 본사를 생성한다.")
    void createHeadquarter() {
        // given
        HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");

        // when
        HeadquarterResDto headquarter = headquarterService.createHeadquarter(headquarterReqDto);

        // then
        assertThat(headquarter.franchiseName()).isEqualTo("test");
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
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long id = savedHeadquarter.getId();

        // when
        HeadquarterResDto headquarterResDto = headquarterService.getHeadquarter(id);

        // then
        assertThat(headquarterResDto.franchiseName()).isEqualTo("test");
    }

    @Test
    @DisplayName("존재하지 않는 본사를 조회할 경우 예외를 던진다.")
    void getHeadquarter_notFound() {
        // given
        Long id = 10L;

        // when, then
        assertThatThrownBy(() -> headquarterService.getHeadquarter(id))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.HEADQUARTER_NOT_FOUND);
    }

    @Test
    @DisplayName("성공적으로 프랜차이즈 이름을 수정한다.")
    void updateHeadquarterName() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long id = savedHeadquarter.getId();

        // when
        HeadquarterResDto updatedHeadquarter = headquarterService.updateHeadquarterName(id, HeadquarterReqDto.of("newTest", "testCategory", "testSubCategory"));

        // then
        assertThat(updatedHeadquarter.franchiseName()).isEqualTo("newTest");
    }

    @Test
    @DisplayName("성공적으로 본사를 삭제한다.")
    void deleteHeadquarter() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long id = savedHeadquarter.getId();

        // when
        headquarterService.deleteHeadquarter(id);

        // then
        assertThat(headquarterRepository.findById(id).isEmpty()).isTrue();
    }
}