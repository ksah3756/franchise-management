package com.goorm.api.common.event;


import com.goorm.api.auth.domain.RefreshToken;
import com.goorm.api.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenSaveEventListener {
    private final RefreshTokenRepository refreshTokenRepository;

    @Async
    @EventListener
    @Retryable(
            value = { RedisConnectionFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, random = true)
    )
    public void handleRefreshTokenSaveEvent(RefreshTokenSaveEvent event) {
        RefreshToken refreshToken = RefreshToken.of(event.getRefreshToken(), event.getId(), event.getUserRole());
        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken saved. username: {}", event.getId());
    }

    @Recover
    public void recover(RedisConnectionFailureException ex, RefreshTokenSaveEvent event) {
        log.error("RefreshToken Redis insertion failed: {}, event: {}", ex.getMessage(), event);
    }
}
