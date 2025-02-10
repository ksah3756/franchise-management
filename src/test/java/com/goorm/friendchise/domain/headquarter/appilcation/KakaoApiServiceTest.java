package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@SpringBootTest
class KakaoApiServiceTest {

    @Autowired
    private KakaoApiService storeRecommendationService;

    // api key에 ip 허용이 안되어서 아직 카카오 API 호출을 못함
    @Test
    void getTotalPlaceData() {
//        Mono<Map<String, KakaoApiResultDto>> totalPlaceData = storeRecommendationService.getTotalPlaceData(List.of("백화점", "학교"), 37.55185670851289, 126.96979548002724);
//        totalPlaceData.block();
    }
}