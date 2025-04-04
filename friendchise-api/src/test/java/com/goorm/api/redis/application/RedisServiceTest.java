package com.goorm.api.redis.application;//package com.goorm.friendchise.domain.redis.application;
//
//
//import com.goorm.friendchise.domain.manager.domain.Role;
//import com.goorm.friendchise.domain.redis.config.RedisConfigTest;
//
//import com.goorm.friendchise.global.auth.domain.RefreshToken;
//import com.goorm.friendchise.global.auth.infrastructure.RedisRefreshTokenRepository;
//import com.goorm.friendchise.global.config.RedisConfig;
//import com.goorm.friendchise.global.redis.RedisService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.context.annotation.Import;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataRedisTest
//@Import(RedisConfigTest.class)
//public class RedisServiceTest {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private RedisRefreshTokenRepository redisRefreshTokenRepository;
//    @Test
//    void testRedisConnection() {
//
//        String testKey = "test:key";
//        String testValue = "Hello, Redis!";
//
//
//        redisTemplate.opsForValue().set(testKey, testValue);
//
//
//        String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
//
//
//        assertThat(retrievedValue).isEqualTo(testValue);
//
//        redisTemplate.delete(testKey);
//    }
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