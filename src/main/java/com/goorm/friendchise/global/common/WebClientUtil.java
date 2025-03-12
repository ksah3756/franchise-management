package com.goorm.friendchise.global.common;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

public class WebClientUtil {
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(3);
    private static final int RETRY_COUNT = 3;
    private static final Duration RETRY_BACKOFF_DURATION = Duration.ofMillis(200);
    private static final double JITTER_FACTOR = 0.5;

    public static <T> Mono<T> applyRetryAndTimeout(Mono<T> mono) {
        return mono
                .timeout(TIMEOUT_DURATION)
                .retryWhen(Retry.backoff(RETRY_COUNT, RETRY_BACKOFF_DURATION)
                        .filter(throwable -> !(throwable instanceof CustomException)) // CustomException 발생 시 재시도하지 않음
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new CustomException(ErrorCode.WEBCLIENT_ERROR, "외부 API 재시도 횟수 초과로 호출 실패"))
                        .jitter(JITTER_FACTOR));
    }

    public static <T> Flux<T> applyRetryAndTimeout(Flux<T> flux) {
        return flux
                .timeout(TIMEOUT_DURATION)
                .retryWhen(Retry.backoff(RETRY_COUNT, RETRY_BACKOFF_DURATION)
                        .filter(throwable -> !(throwable instanceof CustomException)) // CustomException 발생 시 재시도하지 않음
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new CustomException(ErrorCode.WEBCLIENT_ERROR, "외부 API 재시도 횟수 초과로 호출 실패"))
                        .jitter(JITTER_FACTOR));
    }
}
