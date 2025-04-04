package com.goorm.api.user.implement;

import com.goorm.api.user.infrastructure.FakeBcryptEncoder;
import com.goorm.api.user.infrastructure.FakeUserRepository;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UserWriterTest {
    private UserWriter userWriter;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        userWriter = new UserWriter(userRepository, new FakeBcryptEncoder());
    }

    @Test
    @DisplayName("유저를 생성한다.")
    void createUser() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        // when
        User savedUser = userWriter.createUser(user.getUsername(), user.getPassword(), user.getUserRole());
        // then
        userRepository.findById(savedUser.getId())
                .ifPresentOrElse(
                        foundUser -> assertEquals(savedUser.getUsername(), foundUser.getUsername()),
                        () -> fail("유저를 찾을 수 없습니다.")
                );
    }

    @Test
    @DisplayName("이미 존재하는 유저 이름으로 유저를 생성하면 예외를 던진다.")
    void createUser_duplicateName() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        User savedUser = userWriter.createUser(user.getUsername(), user.getPassword(), user.getUserRole());

        // when, then
        User newUser = User.create("테스트", "1234", UserRole.HEADQUARTER);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userWriter.createUser(newUser.getUsername(), newUser.getPassword(), newUser.getUserRole()));
        assertEquals(ex.getMessage(), "이미 존재하는 유저 이름입니다.");
    }

    @Test
    @DisplayName("유저의 비밀번호를 변경한다.")
    void updatePassword() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        // when
        userWriter.updatePassword(user, "5678");
        // then
        assertEquals("5678", user.getPassword());
    }

    @Test
    @DisplayName("유저를 삭제한다.")
    void deleteUser() {
        // given
        User user = User.create("테스트", "1234", UserRole.HEADQUARTER);
        User savedUser = userRepository.save(user);
        // when
        userWriter.deleteUser(savedUser);
        // then
        assertThrows(NoSuchElementException.class, () -> userRepository.findById(savedUser.getId()).orElseThrow());
    }
}