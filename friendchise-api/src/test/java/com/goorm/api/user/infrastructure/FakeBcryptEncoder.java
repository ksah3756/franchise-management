package com.goorm.api.user.infrastructure;

import org.springframework.security.crypto.password.PasswordEncoder;

public class FakeBcryptEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return String.valueOf(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
