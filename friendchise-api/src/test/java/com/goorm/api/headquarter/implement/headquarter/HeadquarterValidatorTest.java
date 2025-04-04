package com.goorm.api.headquarter.implement.headquarter;

import com.goorm.api.headquarter.dto.headquarter.HeadquarterCreateRequest;
import com.goorm.api.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeadquarterValidatorTest {

    private HeadquarterValidator headquarterValidator;
    private HeadquarterRepository headquarterRepository;
    private User user;

    @BeforeEach
    void setUp() {
        headquarterRepository = new FakeHeadquarterRepository();
        headquarterValidator = new HeadquarterValidator(headquarterRepository);
        user = User.builder()
                .id(1L)
                .username("test")
                .password("1234")
                .userRole(UserRole.HEADQUARTER)
                .build();
    }
    @Test
    @DisplayName("본사 이름이 중복될 경우 예외를 반환한다.")
    void checkIfFranchiseNameExists() {
        // given
        HeadquarterCreateRequest headquarterCreateRequest1 = HeadquarterCreateRequest.of("테스트", "패스트푸드", "");
        headquarterRepository.save(HeadquarterCreateRequest.toEntity(user.getId(), headquarterCreateRequest1));

        // when
        HeadquarterCreateRequest headquarterCreateRequest2 = HeadquarterCreateRequest.of("테스트", "일식", "초밥");

        // then
        assertThrows(Exception.class, () -> headquarterValidator.checkIfFranchiseNameExists(headquarterCreateRequest2));
    }

    @Test
    @DisplayName("인증 번호가 일치한다.")
    void validateCertificationNumber_equals() {
        // given
        HeadquarterCreateRequest headquarterCreateRequest1 = HeadquarterCreateRequest.of("테스트", "패스트푸드", "");
        Headquarter savedHeadquarter = headquarterRepository.save(HeadquarterCreateRequest.toEntity(user.getId(), headquarterCreateRequest1));
        String certificationNumber = savedHeadquarter.getCertificationNumber();

        // when, then
        headquarterValidator.validateCertificationNumber(savedHeadquarter.getId(), certificationNumber);
    }

    @Test
    @DisplayName("인증 번호가 일치하지 않는다.")
    void validateCertificationNumber_notEquals() {
        // given
        HeadquarterCreateRequest headquarterCreateRequest1 = HeadquarterCreateRequest.of("테스트", "패스트푸드", "");
        Headquarter savedHeadquarter = headquarterRepository.save(HeadquarterCreateRequest.toEntity(user.getId(), headquarterCreateRequest1));
        String certificationNumber = savedHeadquarter.getCertificationNumber();

        // when, then
        assertThrows(CustomException.class, () -> headquarterValidator.validateCertificationNumber(savedHeadquarter.getId(), "1234"));
    }
}