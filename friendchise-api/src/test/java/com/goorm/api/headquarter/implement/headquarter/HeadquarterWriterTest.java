package com.goorm.api.headquarter.implement.headquarter;

import com.goorm.api.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import com.goorm.core.headquarter.domain.RestaurantCategory;
import com.goorm.core.headquarter.domain.RestaurantSubCategory;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeadquarterWriterTest {
    private HeadquarterRepository headquarterRepository;
    private HeadquarterWriter headquarterWriter;
    private User user;

    @BeforeEach
    void setUp() {
        headquarterRepository = new FakeHeadquarterRepository();
        headquarterWriter = new HeadquarterWriter(headquarterRepository);
        user = User.create("test", "1234", UserRole.HEADQUARTER);
    }

    @Test
    @DisplayName("성공적으로 본사를 생성한다.")
    void createHeadquarter() {
        //given
        Headquarter headquarter = Headquarter.create(user.getId(), "테스트", RestaurantCategory.FASTFOOD, RestaurantSubCategory.NONE);

        // when
        Headquarter createdHeadquarter = headquarterWriter.createHeadquarter(headquarter);

        // then
        Assertions.assertThat(createdHeadquarter.getFranchiseName()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("성공적으로 본사를 수정한다.")
    void updateHeadquarter() {
        // given
        Headquarter headquarter = Headquarter.create(user.getId(), "테스트", RestaurantCategory.FASTFOOD, RestaurantSubCategory.NONE);
        Headquarter newHeadquarter = Headquarter.forUpdate("테스트2", RestaurantCategory.FASTFOOD, RestaurantSubCategory.NONE);

        // when
        headquarterWriter.updateHeadquarter(headquarter, newHeadquarter);

        // then
        Assertions.assertThat(headquarter.getFranchiseName()).isEqualTo("테스트2");
    }

    @Test
    @DisplayName("성공적으로 본사를 삭제한다.")
    void deleteHeadquarter() {
        // given
        Headquarter headquarter = Headquarter.create(user.getId(), "테스트", RestaurantCategory.FASTFOOD, RestaurantSubCategory.NONE);
        headquarterRepository.save(headquarter);

        // when
        headquarterWriter.deleteHeadquarter(headquarter.getId());

        // then
        Assertions.assertThat(headquarterRepository.findById(headquarter.getId())).isEmpty();
    }
}