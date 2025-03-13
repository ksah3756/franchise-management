package com.goorm.friendchise.domain.headquarter.business;

import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialArea;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaReader;
import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.LocalAnalysisRequest;
import com.goorm.friendchise.domain.headquarter.implement.MapDataReader;
import com.goorm.friendchise.domain.headquarter.implement.OpenAiLocalDataAnalyzer;
import com.goorm.friendchise.domain.manager.domain.Manager;
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

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LocalAnalysisServiceTest {
    @Mock
    private MapDataReader mapDataReader;

    @Mock
    private OpenAiLocalDataAnalyzer openAiLocalDataAnalyzer;

    @Mock
    private CommercialAreaReader commercialAreaReader;

    @Mock
    private HeadquarterService headquarterService;

    @InjectMocks
    private LocalAnalysisService localAnalysisService;

    private Manager createManagerWithoutManageId() {
        return Manager.builder()
                .id(1L)
                .username("test")
                .password("test1234")
                .role(HEADQUARTER)
                .build();
    }

    // 이게 맞는건지 모르겠다...
    @Test
    @DisplayName("매장 입점 추천 여부를 받아온다.")
    void getRecommendation() {
        Manager manager = createManagerWithoutManageId();
        // given
        given(commercialAreaReader.getCommercialArea(anyDouble(), anyDouble())).willReturn(CommercialArea.builder()
                .areaName("test")
                .rentalFee(BigDecimal.valueOf(100000))
                .geom(new Polygon(null, null, new GeometryFactory()))
                .build());

        given(headquarterService.getHeadquarterByContext(manager)).willReturn(Headquarter.builder()
                .franchiseName("맥도날드")
                .category(Category.FASTFOOD)
                .subCategory(SubCategory.NONE)
                .build());

        given(mapDataReader.getTotalPlaceData(anyString(), eq(Category.FASTFOOD), eq(SubCategory.NONE), anyList(), anyDouble(), anyDouble()))
                .willReturn(Mono.just(Map.of("반경 1km 내 동일 업종 경쟁 매장", "123",
                        "반경 200m 내 버스정류장", "120",
                        "반경 500m 내 지하철역", "456",
                        "반경 500m 내 대형마트", "222"
                )));
        given(openAiLocalDataAnalyzer.getLocalDataAnalysis(anyString())).willReturn(List.of("test1", "test2"));

        // when
        List<String> testResult = localAnalysisService.getRecommendation(manager, new LocalAnalysisRequest(List.of("대형마트"), 126.2132132, 37.1231231));

        // then
        Assertions.assertThat(testResult).containsExactly("test1", "test2");
    }
}