package com.goorm.friendchise.global.event;

import com.goorm.friendchise.domain.manager.domain.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RefreshTokenSaveEvent {
    private final String refreshToken;
    private final Long userId;
    private final Role role;

    public static RefreshTokenSaveEvent create(String refreshToken, Long userId, Role role) {
        return new RefreshTokenSaveEvent(refreshToken, userId, role);
    }
}
