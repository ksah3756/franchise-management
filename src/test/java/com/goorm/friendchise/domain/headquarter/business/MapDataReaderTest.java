package com.goorm.friendchise.domain.headquarter.business;

import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.CategoryGroupCode;
import com.goorm.friendchise.domain.headquarter.implement.LocalDataReader;
import com.goorm.friendchise.domain.headquarter.implement.MapDataReader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MapDataReaderTest {
    @Mock
    private LocalDataReader localDataReader;

    @InjectMocks
    private MapDataReader mapDataReader;

    // TODO: FakeMapApiClient를 만들어서 모킹 없이 테스트 진행하기
    @Test
    @DisplayName("반경 500m 내 동일한 프랜차이즈 매장이 존재하지 않는 경우 카카오 API로부터 데이터를 가져온다.")
    void getTotalPlaceData_FranchiseStoreNotExists() {
        // given
        given(localDataReader.getSameFranchiseStore(anyString(), anyDouble(), anyDouble(), eq(500)))
                .willReturn(""); // 동일 프랜차이즈 매장이 없다고 가정

        String categoryDummyResult = "123, 342";
        given(localDataReader.getCompetitiveStore(eq(SubCategory.SUSHI.getValue()), anyDouble(), anyDouble(), eq(1000)))
                .willReturn(Mono.just(categoryDummyResult));

        String busStopDummy = "111, 222";
        given(localDataReader.getBusStation(eq("버스정류장"), anyDouble(), anyDouble(), eq(200)))
                .willReturn(Mono.just(busStopDummy));

        String subwayDummy = "333, 444";
        given(localDataReader.getSubwayStation(eq(CategoryGroupCode.SUBWAY.getCode()), anyDouble(), anyDouble(), eq(500)))
                .willReturn(Mono.just(subwayDummy));

        String userCategoryDummyResult = "555, 666";
        given(localDataReader.getUserSelectedInfra(eq(CategoryGroupCode.MART.getCode()), anyDouble(), anyDouble(), eq(500)))
                .willReturn(Mono.just(userCategoryDummyResult));

        // when
        Mono<Map<String, String>> totalPlaceData = mapDataReader.getTotalPlaceData(
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
        given(localDataReader.getSameFranchiseStore(anyString(), anyDouble(), anyDouble(), eq(500)))
                .willReturn("120"); // 동일 프랜차이즈 매장이 있음
        // when
        Mono<Map<String, String>> totalPlaceData = mapDataReader.getTotalPlaceData(
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