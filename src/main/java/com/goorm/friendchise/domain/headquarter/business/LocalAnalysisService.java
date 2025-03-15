package com.goorm.friendchise.domain.headquarter.business;

import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialArea;
import com.goorm.friendchise.domain.headquarter.commercialarea.CommercialAreaReader;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.implement.analyzer.LocalDataAnalyzer;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.LocalAnalysisRequest;
import com.goorm.friendchise.domain.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.friendchise.domain.headquarter.implement.map.MapDataReader;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.aop.ExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalAnalysisService {
    private final MapDataReader mapDataReader;
    private final LocalDataAnalyzer localDataAnalyzer;
    private final CommercialAreaReader commercialAreaReader;
    private final HeadquarterReader headquarterReader;

    /*
     * 사용자의 좌표를 받아 카카오 API로부터 주변 매장 데이터를 가져와 OpenAI API에 요청하여 추천 점수를 받아온다.
     * @param req 사용자의 좌표
     * @return ChatCompletionResponseDto
     */
    @ExecutionTime
    public List<String> getRecommendation(Manager currentManager, LocalAnalysisRequest req) {
        // franchiseName, category, subCategory SecurityContextHolder 에서 가져와서 keyword로 사용
        StringBuilder sb = new StringBuilder();
        CommercialArea area = commercialAreaReader.getCommercialArea(req.x(), req.y());
        sb.append("m² 당 임대료: ").append(area.getRentalFee()).append("\n");
        
        Headquarter headquarter = headquarterReader.getHeadquarterByManager(currentManager);

        List<String> userSelectedCategory = getUserSelectedCategory(req);
        Mono<Map<String, String>> mono = mapDataReader.getTotalPlaceData(
                headquarter.getFranchiseName(),
                headquarter.getCategory(),
                headquarter.getSubCategory(),
                userSelectedCategory,
                req.y(),
                req.x());

        if(mono == null) {
            return List.of("반경 500m 내 동일한 프랜차이즈 매장이 존재합니다.");
        }

        Map<String, String> placeData = mono.block();

        // 지도 API로부터 받아온 데이터를 LLM API에 넘길 데이터로 파싱
        placeData.forEach((key, value) -> {
            sb.append(key).append(": [").append(value).append("]\n");
        });

        String data = sb.toString();
        log.info("LLM api에 사용될 데이터 메시지: {}", data);

        return localDataAnalyzer.getLocalDataAnalysis(data);
    }

    public Flux<String> getRecommendationStream(Manager manager, LocalAnalysisRequest req) {
        // franchiseName, category, subCategory SecurityContextHolder 에서 가져와서 keyword로 사용
        StringBuilder sb = new StringBuilder();
        CommercialArea area = commercialAreaReader.getCommercialArea(req.x(), req.y());
        sb.append("m² 당 임대료: ").append(area.getRentalFee()).append("\n");

        Headquarter headquarter = headquarterReader.getHeadquarterByManager(manager);

        List<String> userSelectedCategory = getUserSelectedCategory(req);
        Mono<Map<String, String>> mono = mapDataReader.getTotalPlaceData(
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

        return localDataAnalyzer.getLocalDataAnalysisStream(data);
    }

    public List<String> getRecommendationDummy(LocalAnalysisRequest req) throws InterruptedException {
        // 카카오 API 호출
        Thread.sleep(100);
        // OpenAI API 호출
        Thread.sleep(5000);

        return List.of("추천(비추천) - [XX점], 예상 월 매출: xxx만원, m²당 임대료: xxx원\n" +
                "장점:  \n" +
                "- (필요한 경우 첫번째 장점)\n" +
                "- (필요한 경우 두번째 장점)\n" +
                "- (필요한 경우 세번째 장점)\n" +
                "단점:  \n" +
                "- (필요한 경우 첫번째 단점)\n" +
                "- (필요한 경우 두번째 단점)\n" +
                "- (필요한 경우 세번째 단점)\n");
    }

    public Flux<String> getRecommendationStreamDummy(LocalAnalysisRequest req) throws InterruptedException {
        // 카카오 API 호출
        Thread.sleep(100);
        // OpenAI API 호출
        Thread.sleep(100);

        String result = String.join("", Collections.nCopies(300, "s"));
        return Flux.just(result).delayElements(Duration.ofMillis(10));
    }

    private static List<String> getUserSelectedCategory(LocalAnalysisRequest req) {
        List<String> userSelectedCategory;
        if(req.userSelectedCategory() == null) userSelectedCategory = List.of();
        else userSelectedCategory = req.userSelectedCategory();
        return userSelectedCategory;
    }
}
