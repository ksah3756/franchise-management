package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialArea;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaRepository;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaService;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.StoreRecommendReqDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto.Choice;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatMessage;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRecommendationService {
    private final KakaoApiService kakaoApiService;
    private final OpenAiApiService openAiApiService;
    private final CommercialAreaService commercialAreaService;
    private final HeadquarterService headquarterService;

    /*
     * 사용자의 좌표를 받아 카카오 API로부터 주변 매장 데이터를 가져와 OpenAI API에 요청하여 추천 점수를 받아온다.
     * @param req 사용자의 좌표
     * @return ChatCompletionResponseDto
     */
    public ChatCompletionResponseDto getRecommendation(StoreRecommendReqDto req) {
        // franchiseName, category, subCategory SecurityContextHolder 에서 가져와서 keyword로 사용
        StringBuilder sb = new StringBuilder();
        CommercialArea area = commercialAreaService.getCommercialArea(req.x(), req.y());
        sb.append("m² 당 임대료: ").append(area.getRentalFee()).append("\n");
        
        Headquarter headquarter = headquarterService.getHeadquarterByContext();

        List<String> userSelectedCategory = getUserSelectedCategory(req);
        Mono<Map<String, KakaoApiResultDto>> mono = kakaoApiService.getTotalPlaceData(
                headquarter.getFranchiseName(),
                headquarter.getCategory(),
                headquarter.getSubCategory(),
                userSelectedCategory,
                req.y(),
                req.x());

        if(mono == null) { // 반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우
            Choice choice = Choice.of(ChatMessage.of("assistant", "반경 500m 내 동일한 프랜차이즈 매장이 존재합니다."));
            return ChatCompletionResponseDto.of(List.of(choice), new ChatCompletionResponseDto.Usage(0, 0));
        }

        Map<String, KakaoApiResultDto> totalPlaceData = mono.block();

        // 카카오 API로부터 받아온 데이터를 OpenAI API에 넘길 데이터로 파싱
        totalPlaceData.forEach((key, value) -> {
            List<KakaoPlaceDto> documents = value.documents();
            String distances = documents.stream()
                    .map(KakaoPlaceDto::distance)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            sb.append(key).append(": [").append(distances).append("]\n");
        });

        String data = sb.toString();
        log.info("openAi api에 사용될 데이터 메시지: {}", data);

        ChatCompletionResponseDto res = openAiApiService.requestChatCompletion(data);
        int totalTokens = res.usage().getCompletionTokens() + res.usage().getPromptTokens();
        log.info("전체 사용 토큰 수: {}", totalTokens);
        return res;
    }

    private static List<String> getUserSelectedCategory(StoreRecommendReqDto req) {
        List<String> userSelectedCategory;
        if(req.userSelectedCategory() == null) userSelectedCategory = List.of();
        else userSelectedCategory = req.userSelectedCategory();
        return userSelectedCategory;
    }
}
