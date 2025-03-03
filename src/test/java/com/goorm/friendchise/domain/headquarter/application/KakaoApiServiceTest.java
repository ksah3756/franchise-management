package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.CategoryGroupCode;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KakaoApiServiceTest {
    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private KakaoApiService kakaoApiService;

    @Test
    @DisplayName("반경 500m 내 동일한 프랜차이즈 매장이 존재하지 않는 경우 카카오 API로부터 데이터를 가져온다.")
    void getTotalPlaceData_FranchiseStoreNotExists() {
        // given
        given(kakaoApiClient.requestPlaceDataByKeywordSync(anyString(), anyDouble(), anyDouble(), eq(500)))
                .willReturn(new KakaoApiResultDto(Collections.emptyList())); // 동일 프랜차이즈 매장이 없다고 가정

        KakaoApiResultDto categoryDummyResult = new KakaoApiResultDto(List.of(new KakaoPlaceDto("123")));
        given(kakaoApiClient.requestPlaceDataByKeywordAsync(eq(SubCategory.SUSHI.getValue()), anyDouble(), anyDouble(), eq(1000)))
                .willReturn(Mono.just(categoryDummyResult));

        KakaoApiResultDto busStopDummy = new KakaoApiResultDto(List.of(new KakaoPlaceDto("120")));
        given(kakaoApiClient.requestPlaceDataByKeywordAsync(eq("버스정류장"), anyDouble(), anyDouble(), eq(200)))
                .willReturn(Mono.just(busStopDummy));

        KakaoApiResultDto subwayDummy = new KakaoApiResultDto(List.of(new KakaoPlaceDto("456")));
        given(kakaoApiClient.requestPlaceDataByCategoryAsync(eq(CategoryGroupCode.SUBWAY.getCode()), anyDouble(), anyDouble(), eq(500)))
                .willReturn(Mono.just(subwayDummy));

        KakaoApiResultDto userCategoryDummyResult = new KakaoApiResultDto(List.of(new KakaoPlaceDto("222")));
        given(kakaoApiClient.requestPlaceDataByCategoryAsync(eq(CategoryGroupCode.MART.getCode()), anyDouble(), anyDouble(), eq(500)))
                .willReturn(Mono.just(userCategoryDummyResult));

        // when
        Mono<Map<String, KakaoApiResultDto>> totalPlaceData = kakaoApiService.getTotalPlaceData(
                "testFranchise",
                Category.JAPANESEFOOD,
                SubCategory.SUSHI,
                List.of("notExistCategory", "대형마트"),
                37.55185670851289,
                126.96979548002724
        );

        // then
        assertThat(totalPlaceData).isNotNull();
        totalPlaceData.subscribe(result -> {
            assertThat(result).containsKeys(
                    "반경 1km 내 동일 업종 경쟁 매장",
                    "반경 200m 내 버스정류장",
                    "반경 500m 내 지하철역",
                    "반경 500m 내 대형마트"
            );
            assertThat(result.get("반경 1km 내 동일 업종 경쟁 매장")).isEqualTo(categoryDummyResult);
            assertThat(result.get("반경 200m 내 버스정류장")).isEqualTo(busStopDummy);
            assertThat(result.get("반경 500m 내 지하철역")).isEqualTo(subwayDummy);
            assertThat(result.get("반경 500m 내 대형마트")).isEqualTo(userCategoryDummyResult);
        });
    }

    @Test
    @DisplayName("반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우 null을 반환한다.")
    void getTotalPlaceData_FranchiseStoreExist() {
        // given
        given(kakaoApiClient.requestPlaceDataByKeywordSync(anyString(), anyDouble(), anyDouble(), eq(500)))
                .willReturn(new KakaoApiResultDto(List.of(new KakaoPlaceDto("120")))); // 동일 프랜차이즈 매장이 있음
        // when
        Mono<Map<String, KakaoApiResultDto>> totalPlaceData = kakaoApiService.getTotalPlaceData(
                "맥도날드",
                Category.FASTFOOD,
                SubCategory.NONE,
                List.of("백화점", "학교"),
                37.55185670851289,
                126.96979548002724
        );

        // then
        assertThat(totalPlaceData).isNull();
    }
}