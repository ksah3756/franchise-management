package com.goorm.api.user.implement;

import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWriter {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public User createUser(String username, String password, UserRole userRole) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return userRepository.save(User.create(username, encodedPassword, userRole));
    }

    public void updatePassword(User user, String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.updatePassword(encodedPassword);
    }
    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }
}
