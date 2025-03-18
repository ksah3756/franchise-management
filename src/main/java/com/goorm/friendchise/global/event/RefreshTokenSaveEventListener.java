package com.goorm.friendchise.global.event;

import com.goorm.friendchise.global.aop.ExecutionTime;
import com.goorm.friendchise.global.auth.domain.RefreshToken;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
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
    @ExecutionTime
    public void handleRefreshTokenSaveEvent(RefreshTokenSaveEvent event) {
        RefreshToken refreshToken = RefreshToken.of(event.getRefreshToken(), event.getUserId(), event.getRole());
        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken이 저장되었습니다. userId: {}", event.getUserId());
    }

    @Recover
    public void recover(RedisConnectionFailureException ex, RefreshTokenSaveEvent event) {
        log.error("RefreshToken Redis 저장 재시도 실패: {}, event: {}", ex.getMessage(), event);
    }
}
