//package com.goorm.friendchise.domain.headquarter.application;
//
//import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class KakaoApiServiceTest {
//
//    @Autowired
//    private KakaoApiService kakaoApiService;
//
//    // api key에 ip 허용이 안되어서 아직 카카오 API 호출을 못함
//    @Test
//    void getTotalPlaceData() {
//        Mono<Map<String, KakaoApiResultDto>> totalPlaceData = kakaoApiService.getTotalPlaceData(List.of("백화점", "학교"), 37.55185670851289, 126.96979548002724);
//        Map<String, KakaoApiResultDto> result = totalPlaceData.block();
//        System.out.println(result);
//    }
//
//    @Test
//    @DisplayName("반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우")
//    void getTotalPlaceData_FranchiseStoreExist() {
//        Mono<Map<String, KakaoApiResultDto>> totalPlaceData = kakaoApiService.getTotalPlaceData(List.of("백화점", "학교"), 37.49864658673187, 127.02873329209403);
//        Assertions.assertThat(totalPlaceData).isNull();
//    }
//
//    @Test
//    @DisplayName("반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우")
//    void 테스트() {
//        KakaoApiResultDto result = kakaoApiService.requestPlaceDataByKeywordSync("맥도날드", 37.49864658673187, 127.02873329209403, 500);
//        System.out.println(result);
//    }
//}