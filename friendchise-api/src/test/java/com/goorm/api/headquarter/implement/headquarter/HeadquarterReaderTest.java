package com.goorm.api.headquarter.implement.headquarter;

import com.goorm.api.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeadquarterReaderTest {

    private HeadquarterRepository headquarterRepository;
    private HeadquarterReader headquarterReader;
    private User user;

    @BeforeEach
    void setUp() {
        headquarterRepository = new FakeHeadquarterRepository();
        headquarterReader = new HeadquarterReader(headquarterRepository);
        user = User.create("test", "1234", UserRole.HEADQUARTER);
    }

    @Test
    void getHeadquarterById() {

    }

    @Test
    void getHeadquarterByFranchiseName() {
    }
}