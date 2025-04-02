package com.goorm.api.user.application;

import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.api.user.dto.request.UserCreateRequest;
import com.goorm.api.user.dto.request.UserPasswordRequest;
import com.goorm.api.user.dto.response.UserDetailResponse;
import com.goorm.api.user.dto.response.UserPersistResponse;
import com.goorm.api.user.implement.UserReader;
import com.goorm.api.user.implement.UserWriter;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserWriter userWriter;
    private final HeadquarterReader headquarterReader;

    @Transactional
    public UserPersistResponse createUser(UserCreateRequest request) {
        User savedUser = userWriter.createUser(request.username(), request.password(), request.userRole());

        return UserPersistResponse.of(savedUser.getId());
    }

    public UserDetailResponse getUserDetail(User user) {
        if(user.getUserRole() == UserRole.HEADQUARTER) {
            Headquarter headquarter = headquarterReader.getHeadquarterById(user.getId());
            return UserDetailResponse.fromHeadquarter(user, headquarter.getCertificationNumber());
        }
        return UserDetailResponse.from(user);
    }

    @Transactional
    public void updatePassword(User user, String password) {
        userWriter.updatePassword(user, password);
    }

    @Transactional
    public void deleteUser(User user) {
        userWriter.deleteUser(user);
    }
}
