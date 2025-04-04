package com.goorm.api.redis.application;//package com.goorm.friendchise.domain.redis.application;
//
//import com.goorm.friendchise.domain.manager.domain.Role;
//import com.goorm.friendchise.domain.redis.config.RedisConfigTest;
//import com.goorm.friendchise.global.auth.domain.RefreshToken;
//import com.goorm.friendchise.global.auth.infrastructure.RedisRefreshTokenRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//
//import org.springframework.context.annotation.Import;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataRedisTest // Redis 관련 설정만 로드
//@Import(RedisConfigTest.class) // 테스트용 Redis 설정만 Import
//public class RedisRepositoryTest {
//
//    @Autowired
//    RedisRefreshTokenRepository redisRefreshTokenRepository;
//
//    @Test
//    void 리프레스_토큰_저장_및_조회()
//    {
//        // Given (새로운 리프레시 토큰 생성)
//        String testToken = "test-refresh-token-123";
//        RefreshToken refreshToken = RefreshToken.of(testToken, 1L, Role.HEADQUARTER);
//
//        // When (토큰 저장)
//        redisRefreshTokenRepository.save(refreshToken);
//
//        // Then (토큰 조회)
//        Optional<RefreshToken> foundToken = redisRefreshTokenRepository.findByRefreshToken(testToken);
//        assertThat(foundToken).isPresent();
//        assertThat(foundToken.get().getRefreshToken()).isEqualTo(testToken);
//        assertThat(foundToken.get().getId()).isEqualTo(1L);
//        assertThat(foundToken.get().getRole()).isEqualTo(Role.HEADQUARTER);
//    }
//}
