package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialArea;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaService;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.StoreRecommendReqDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto.Choice;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatMessage;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.aop.ExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRecommendationService {
    // 너무 구체적인 구현방식에 의존하는거같은데
    private final MapApiService mapApiService;
    private final OpenAiApiService openAiApiService;
    private final CommercialAreaService commercialAreaService;
    private final HeadquarterService headquarterService;

    /*
     * 사용자의 좌표를 받아 카카오 API로부터 주변 매장 데이터를 가져와 OpenAI API에 요청하여 추천 점수를 받아온다.
     * @param req 사용자의 좌표
     * @return ChatCompletionResponseDto
     */
    @ExecutionTime
    public ChatCompletionResponseDto getRecommendation(Manager currentManager, StoreRecommendReqDto req) {
        // franchiseName, category, subCategory SecurityContextHolder 에서 가져와서 keyword로 사용
        StringBuilder sb = new StringBuilder();
        CommercialArea area = commercialAreaService.getCommercialArea(req.x(), req.y());
        sb.append("m² 당 임대료: ").append(area.getRentalFee()).append("\n");
        
        Headquarter headquarter = headquarterService.getHeadquarterByContext(currentManager);

        List<String> userSelectedCategory = getUserSelectedCategory(req);
        Mono<Map<String, String>> mono = mapApiService.getTotalPlaceData(
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

        Map<String, String> placeData = mono.block();

        // 지도 API로부터 받아온 데이터를 LLM API에 넘길 데이터로 파싱
        placeData.forEach((key, value) -> {
            sb.append(key).append(": [").append(value).append("]\n");
        });

        String data = sb.toString();
        log.info("LLM api에 사용될 데이터 메시지: {}", data);

        ChatCompletionResponseDto res = openAiApiService.requestChatCompletion(data);
        int totalTokens = res.usage().getCompletionTokens() + res.usage().getPromptTokens();
        log.info("전체 사용 토큰 수: {}", totalTokens);
        return res;
    }

    public Flux<String> getRecommendationStream(Manager manager, StoreRecommendReqDto req) {
        // franchiseName, category, subCategory SecurityContextHolder 에서 가져와서 keyword로 사용
        StringBuilder sb = new StringBuilder();
        CommercialArea area = commercialAreaService.getCommercialArea(req.x(), req.y());
        sb.append("m² 당 임대료: ").append(area.getRentalFee()).append("\n");

        Headquarter headquarter = headquarterService.getHeadquarterByContext(manager);

        List<String> userSelectedCategory = getUserSelectedCategory(req);
        Mono<Map<String, String>> mono = mapApiService.getTotalPlaceData(
                headquarter.getFranchiseName(),
                headquarter.getCategory(),
                headquarter.getSubCategory(),
                userSelectedCategory,
                req.y(),
                req.x());

        if(mono == null) { // 반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우
            return Flux.just("반경 500m 내 동일한 프랜차이즈 매장이 존재합니다.");
        }

        Map<String, String> placeData = mono.block();

        // 지도 API로부터 받아온 데이터를 LLM API에 넘길 데이터로 파싱
        placeData.forEach((key, value) -> {
            sb.append(key).append(": [").append(value).append("]\n");
        });

        String data = sb.toString();
        log.info("LLM api에 사용될 데이터 메시지: {}", data);

        return openAiApiService.requestChatCompletionStream(data);
    }

    public ChatCompletionResponseDto getRecommendationDummy(StoreRecommendReqDto req) throws InterruptedException {
        // 카카오 API 호출
        Thread.sleep(100);
        // OpenAI API 호출
        Thread.sleep(5000);

        return ChatCompletionResponseDto.of(List.of(), new ChatCompletionResponseDto.Usage(0, 0));
    }

    public Flux<String> getRecommendationStreamDummy(StoreRecommendReqDto req) throws InterruptedException {
        // 카카오 API 호출
        Thread.sleep(100);
        // OpenAI API 호출
        Thread.sleep(100);

        String result = String.join("", Collections.nCopies(300, "s"));
        return Flux.just(result).delayElements(Duration.ofMillis(10));
    }

    private static List<String> getUserSelectedCategory(StoreRecommendReqDto req) {
        List<String> userSelectedCategory;
        if(req.userSelectedCategory() == null) userSelectedCategory = List.of();
        else userSelectedCategory = req.userSelectedCategory();
        return userSelectedCategory;
    }
}
