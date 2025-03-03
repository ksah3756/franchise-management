package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.CategoryGroupCode;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoApiService {
    private final KakaoApiClient kakaoApiClient;

    public Mono<Map<String, KakaoApiResultDto>> getTotalPlaceData(
            String franchiseName,
            Category category,
            SubCategory subCategory,
            List<String> userSelectedCategory,
            Double y,
            Double x
    ) {
        // 1. 동일 프랜차이즈 매장 검색
        KakaoApiResultDto sameFranchiseStoreResult = kakaoApiClient.requestPlaceDataByKeywordSync(franchiseName, y, x, 500);
        if(!sameFranchiseStoreResult.documents().isEmpty()) {
            return null; // 동일 프랜차이즈 매장이 존재하면 바로 리턴
        }

        Map<String, Mono<KakaoApiResultDto>> totalSearchResults = new HashMap<>();

        // 2. 동일 업종 경쟁 매장 검색
        Mono<KakaoApiResultDto> sameCategoryStoreResult;
        if(subCategory.equals(SubCategory.NONE)) { // subCategory가 없으면 category로 검색
            sameCategoryStoreResult =  kakaoApiClient.requestPlaceDataByKeywordAsync(category.getValue(), y, x, 1000);
        } else {
            sameCategoryStoreResult = kakaoApiClient.requestPlaceDataByKeywordAsync(subCategory.getValue(), y, x, 1000);
        }
        totalSearchResults.put("반경 1km 내 동일 업종 경쟁 매장", sameCategoryStoreResult);

        // 3. 버스 정류장, 지하철역 검색
        Mono<KakaoApiResultDto> busStopResult = kakaoApiClient.requestPlaceDataByKeywordAsync("버스정류장", y, x, 200);
        Mono<KakaoApiResultDto> subwayStationResult = kakaoApiClient.requestPlaceDataByCategoryAsync(CategoryGroupCode.SUBWAY.getCode(), y, x, 500);
        totalSearchResults.put("반경 200m 내 버스정류장", busStopResult);
        totalSearchResults.put("반경 500m 내 지하철역", subwayStationResult);

        // 4. 사용자가 선택한 카테고리로 검색
        for(String selectedCategory : userSelectedCategory) {
            CategoryGroupCode categoryGroupCode = CategoryGroupCode.fromString(selectedCategory);
            if(categoryGroupCode == null) continue;
            Mono<KakaoApiResultDto> userDefinedResult = kakaoApiClient.requestPlaceDataByCategoryAsync(categoryGroupCode.getCode(), y, x, 500);
            totalSearchResults.put("반경 500m 내 " + categoryGroupCode.getValue(), userDefinedResult);
        }

        // 5. 결과 합치기
        List<Mono<KakaoApiResultDto>> monos = new ArrayList<>(totalSearchResults.values());
        List<String> keys = new ArrayList<>(totalSearchResults.keySet());

        return Mono.zip(monos, results -> {
            Map<String, KakaoApiResultDto> combinedResults = new HashMap<>();
            for (int i = 0; i < results.length; i++) {
                combinedResults.put(keys.get(i), (KakaoApiResultDto) results[i]);
            }
            return combinedResults;
        });

    }
}
