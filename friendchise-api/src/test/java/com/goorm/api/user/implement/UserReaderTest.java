package com.goorm.api.user.implement;

import com.goorm.api.user.infrastructure.FakeUserRepository;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserReaderTest {
    private UserReader userReader;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        userReader = new UserReader(userRepository);
    }

    @Test
    @DisplayName("유저 이름으로 유저를 찾는다.")
    void getUserByUsername() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        User savedUser = userRepository.save(user);

        // when
        User foundUser = userReader.getUserByUsername(savedUser.getUsername());

        // then
        assertEquals(savedUser, foundUser);
    }

    @Test
    @DisplayName("유저 이름으로 유저를 찾지 못하면 예외를 던진다.")
    void getUserByUsername_UserNotFound() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        userRepository.save(user);

        // when, then
        CustomException ex = assertThrows(CustomException.class, () -> userReader.getUserByUsername("테스트2"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}