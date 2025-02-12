package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.dto.headquarter.StoreRecommendReqDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto.Choice;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatMessage;
import com.goorm.friendchise.domain.headquarter.util.JsonHashMapConverter;
import com.goorm.friendchise.domain.headquarter.util.PlaceData;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreRecommendationService {
    private final KakaoApiService kakaoApiService;
    private final OpenAiApiService openAiApiService;

    // 읽기 작업만 하므로 ConcurrentHashMap 대신 unmodifiableMap 사용
    private Map<String, Map<String, List<PlaceData>>> placeDataMap;

    /*
     * 지역별 임대료 데이터인 hDongInfo.json 파일을 읽어 ConcurrentHashMap에 저장한다.
     */
    @PostConstruct
    public void initRentalFeeMap() throws IOException {
        // TODO: 상권에 따른 임대료 데이터 파일을 읽어와서 메모리에 로드한다.
        String jsonFile = "hDongInfo.json";
        placeDataMap = Collections.unmodifiableMap(JsonHashMapConverter.convertJsonToHashmap(jsonFile));
    }


    // TODO: y, x 대신 상권을 String으로 받아서 rentalFeeMap에서 해당 상권의 임대료 데이터를 가져오기
    public ChatCompletionResponseDto getRecommendation(StoreRecommendReqDto req) {
//        List<String> regionData = kakaoApiService.getHdongFromCoord(req.y(), req.x());
//        String guName = regionData.get(0);
//        String hDongName = regionData.get(1);

        List<String> userSelectedCategory = getUserSelectedCategory(req);

        Mono<Map<String, KakaoApiResultDto>> mono = kakaoApiService.getTotalPlaceData(userSelectedCategory, req.y(), req.x());

        if(mono == null) { // 반경 500m 내 동일한 프랜차이즈 매장이 존재할 경우
            Choice choice = Choice.of(ChatMessage.of("assistant", "반경 500m 내 동일한 프랜차이즈 매장이 존재합니다."));
            return ChatCompletionResponseDto.of(List.of(choice), new ChatCompletionResponseDto.Usage(0, 0));
        }

        Map<String, KakaoApiResultDto> totalPlaceData = mono.block();

    //        String address = totalPlaceData.get("반경 200m 내 버스정류장").documents().get(0).addressName();

        // 카카오 API로부터 받은 데이터를 모아서 OpenAI API에 요청하여 추천 점수를 받아온다.
        StringBuilder sb = new StringBuilder();
        totalPlaceData.entrySet().forEach(entry -> {
            List<KakaoPlaceDto> documents = entry.getValue().documents();
            sb.append(entry.getKey()).append(": [");
            documents.stream().map(KakaoPlaceDto::distance).forEach(distance -> sb.append(distance).append(", "));
            sb.append("]\n");
        });
        // TODO: y, x를 행정동으로 변환하여 추정매출, 임대료, 유동인구, 직장인구 등을 가져오기

        return openAiApiService.requestChatCompletionApi(sb.toString());
    }

    private void getPlaceData(String guName, String hDongName) {
        if(!placeDataMap.containsKey(guName) || !placeDataMap.get(guName).containsKey(hDongName)) {
            throw new CustomException(ErrorCode.REGION_NOT_SUPPORTED);
        }
        placeDataMap.get(guName).get(hDongName);
    }

    private static List<String> getUserSelectedCategory(StoreRecommendReqDto req) {
        List<String> userSelectedCategory;
        if(req.userSelectedCategory() == null) userSelectedCategory = List.of();
        else userSelectedCategory = req.userSelectedCategory();
        return userSelectedCategory;
    }
}
