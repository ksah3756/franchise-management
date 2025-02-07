package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
        HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test");

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
                .build();
        headquarterRepository.save(headquarter);

        // when, then
        HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test");
        assertThatThrownBy(() -> headquarterService.createHeadquarter(headquarterReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRANCHISE_NAME_DUPLICATION);
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
    @DisplayName("성공적으로 본사 정보를 수정한다.")
    void updateHeadquarter() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long id = savedHeadquarter.getId();

        // when
        HeadquarterResDto updatedHeadquarter = headquarterService.updateHeadquarter(id, HeadquarterReqDto.of("newTest"));

        // then
        assertThat(updatedHeadquarter.franchiseName()).isEqualTo("newTest");
    }

    @Test
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