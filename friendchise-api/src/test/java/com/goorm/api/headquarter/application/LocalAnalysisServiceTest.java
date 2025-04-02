package com.goorm.api.headquarter.application;

import com.goorm.api.headquarter.dto.headquarter.LocalAnalysisRequest;
import com.goorm.api.headquarter.implement.analyzer.LocalDataAnalyzer;
import com.goorm.api.headquarter.implement.commercialarea.CommercialAreaReader;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.api.headquarter.implement.map.LocalDataProvider;
import com.goorm.core.headquarter.domain.RestaurantCategory;
import com.goorm.core.headquarter.domain.RestaurantSubCategory;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import com.goorm.friendchise.domain.headquarter.domain.CommercialArea;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LocalAnalysisServiceTest {
    @Mock
    private LocalDataProvider localDataProvider;

    @Mock
    private LocalDataAnalyzer localDataAnalyzer;

    @Mock
    private CommercialAreaReader commercialAreaReader;

    @Mock
    private HeadquarterReader headquarterReader;

    @InjectMocks
    private LocalAnalysisService localAnalysisService;

    // 이게 맞는건지 모르겠다...
    @Test
    @DisplayName("매장 입점 추천 여부를 받아온다.")
    void getRecommendation() {
        // given
        given(commercialAreaReader.getCommercialArea(anyDouble(), anyDouble())).willReturn(CommercialArea.builder()
                .areaName("test")
                .rentalFee(BigDecimal.valueOf(100000))
                .geom(new Polygon(null, null, new GeometryFactory()))
                .build());


        given(localDataProvider.getTotalPlaceData(anyString(), eq(RestaurantCategory.FASTFOOD), eq(RestaurantSubCategory.NONE), anyList(), anyDouble(), anyDouble()))
                .willReturn(Mono.just(Map.of("반경 1km 내 동일 업종 경쟁 매장", "123",
                        "반경 200m 내 버스정류장", "120",
                        "반경 500m 내 지하철역", "456",
                        "반경 500m 내 대형마트", "222"
                )));
        given(localDataAnalyzer.getLocalDataAnalysis(anyString())).willReturn(List.of("test1", "test2"));

        User user = User.builder()
                .id(1L)
                .password("test")
                .userRole(UserRole.HEADQUARTER)
                .build();

        // when
        List<String> testResult = localAnalysisService.getRecommendation(user, new LocalAnalysisRequest(List.of("대형마트"), 126.2132132, 37.1231231));

        // then
        Assertions.assertThat(testResult).containsExactly("test1", "test2");
    }
}