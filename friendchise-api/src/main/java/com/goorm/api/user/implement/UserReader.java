package com.goorm.api.user.implement;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
