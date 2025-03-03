package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialArea;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaService;
import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.StoreRecommendReqDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto.Choice;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatMessage;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class StoreRecommendationServiceTest {
    @Mock
    private KakaoApiService kakaoApiService;

    @Mock
    private OpenAiApiService openAiApiService;

    @Mock
    private CommercialAreaService commercialAreaService;

    @Mock
    private HeadquarterService headquarterService;

    @InjectMocks
    private StoreRecommendationService storeRecommendationService;

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
        given(commercialAreaService.getCommercialArea(anyDouble(), anyDouble())).willReturn(CommercialArea.builder()
                .areaName("test")
                .rentalFee(BigDecimal.valueOf(100000))
                .geom(new Polygon(null, null, new GeometryFactory()))
                .build());

        given(headquarterService.getHeadquarterByContext(manager)).willReturn(Headquarter.builder()
                .franchiseName("맥도날드")
                .category(Category.FASTFOOD)
                .subCategory(SubCategory.NONE)
                .build());

        given(kakaoApiService.getTotalPlaceData(anyString(), eq(Category.FASTFOOD), eq(SubCategory.NONE), anyList(), anyDouble(), anyDouble()))
                .willReturn(Mono.just(Map.of("반경 1km 내 동일 업종 경쟁 매장", new KakaoApiResultDto(List.of(new KakaoPlaceDto("123"))),
                        "반경 200m 내 버스정류장", new KakaoApiResultDto(List.of(new KakaoPlaceDto("120"))),
                        "반경 500m 내 지하철역", new KakaoApiResultDto(List.of(new KakaoPlaceDto("456"))),
                        "반경 500m 내 대형마트", new KakaoApiResultDto(List.of(new KakaoPlaceDto("222")))
                )));
        List<Choice> choices = List.of(Choice.of(ChatMessage.of("assistant", "test1")), Choice.of(ChatMessage.of("assistant", "test2")));
        given(openAiApiService.requestChatCompletion(anyString())).willReturn(ChatCompletionResponseDto.of(choices, new ChatCompletionResponseDto.Usage(0, 0)));

        // when

        ChatCompletionResponseDto response = storeRecommendationService.getRecommendation(manager, new StoreRecommendReqDto(List.of("대형마트"), 126.2132132, 37.1231231));

        // then
        Assertions.assertThat(response.choices()).isEqualTo(choices);
    }
}