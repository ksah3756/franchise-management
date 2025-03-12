package com.goorm.friendchise.domain.headquarter.implement;

import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.CategoryGroupCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MapDataReader {
    private final LocalDataReader localDataReader;

    public Mono<Map<String, String>> getTotalPlaceData(
            String franchiseName,
            Category category,
            SubCategory subCategory,
            List<String> userSelectedCategory,
            Double y,
            Double x
    ) {
        // 1. 동일 프랜차이즈 매장 검색
        String sameFranchiseStores = localDataReader.getSameFranchiseStore(franchiseName, y, x, 500);
        if(!sameFranchiseStores.isEmpty()) {
            return null; // 동일 프랜차이즈 매장이 존재하면 바로 리턴
        }

        Map<String, Mono<String>> totalSearchResults = new HashMap<>();

        // 2. 동일 업종 경쟁 매장 검색
        Mono<String> competitiveStores;
        if(subCategory.equals(SubCategory.NONE)) { // subCategory가 없으면 category로 검색
            competitiveStores =  localDataReader.getCompetitiveStore(category.getValue(), x, y, 1000);
        } else {
            competitiveStores = localDataReader.getCompetitiveStore(subCategory.getValue(), x, y, 1000);
        }
        totalSearchResults.put("반경 1km 내 동일 업종 경쟁 매장", competitiveStores);

        // 3. 버스 정류장, 지하철역 검색
        Mono<String> busStations = localDataReader.getBusStation("버스정류장", x, y, 200);
        Mono<String> subwayStations = localDataReader.getSubwayStation(CategoryGroupCode.SUBWAY.getCode(), x, y, 500);
        totalSearchResults.put("반경 200m 내 버스정류장", busStations);
        totalSearchResults.put("반경 500m 내 지하철역", subwayStations);

        // 4. 사용자가 선택한 카테고리로 검색
        for(String selectedCategory : userSelectedCategory) {
            CategoryGroupCode categoryGroupCode = CategoryGroupCode.fromString(selectedCategory);
            if(categoryGroupCode == null) continue;
            Mono<String> userSelectedInfras = localDataReader.getUserSelectedInfra(categoryGroupCode.getCode(), x, y, 500);
            totalSearchResults.put("반경 500m 내 " + categoryGroupCode.getValue(), userSelectedInfras);
        }

        // 5. 결과 합치기
        List<Mono<String>> monos = new ArrayList<>(totalSearchResults.values());
        List<String> keys = new ArrayList<>(totalSearchResults.keySet());

        return Mono.zip(monos, results -> {
            Map<String, String> combinedResults = new HashMap<>();
            for (int i = 0; i < results.length; i++) {
                combinedResults.put(keys.get(i), (String) results[i]);
            }
            return combinedResults;
        });

    }
}
