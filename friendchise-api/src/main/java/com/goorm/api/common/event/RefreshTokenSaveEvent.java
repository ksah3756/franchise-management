package com.goorm.api.common.event;

import com.goorm.core.user.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RefreshTokenSaveEvent {
    private final String refreshToken;
    private final String id;
    private final UserRole userRole;

    public static RefreshTokenSaveEvent create(String refreshToken, String id, UserRole userRole) {
        return new RefreshTokenSaveEvent(refreshToken, id, userRole);
    }
}
