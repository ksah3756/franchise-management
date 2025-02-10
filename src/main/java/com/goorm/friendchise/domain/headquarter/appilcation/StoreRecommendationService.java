package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreRecommendationService {
    private final KakaoApiService kakaoApiService;
    private final OpenAiApiService openAiApiService;

    // 읽기 작업만 하므로 ConcurrentHashMap 대신 unmodifiableMap 사용
    private Map<String, BigDecimal> rentalFeeMap;

    /*
     * 지역별 임대료 데이터인 rentalData.json 파일을 읽어 ConcurrentHashMap에 저장한다.
     */
    @PostConstruct
    public void initRentalFeeMap() throws IOException {
        // TODO: 상권에 따른 임대료 데이터 파일을 읽어와서 메모리에 로드한다.
//        String jsonFile = "rentalData.json";
//        ObjectMapper objectMapper = new ObjectMapper();
//        ConcurrentHashMap<String, BigDecimal> map = new ConcurrentHashMap<>();
//
//        // JSON 파일 파싱하여 ConcurrentHashMap에 저장
//        JsonNode rootNode = objectMapper.readTree(new File(jsonFile));
//        JsonNode dataNode = rootNode.path("sheet").path("1").path("data");
//        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
//        while (fields.hasNext()) {
//            Map.Entry<String, JsonNode> entry = fields.next();
//            JsonNode dataEntryNode = entry.getValue();
//
//            // ex. 강남
//            String location = dataEntryNode.path("2").asText();
//            // ex. 한티역
//            String subLocation = dataEntryNode.path("3").asText();
//            BigDecimal rentalFee = new BigDecimal(dataEntryNode.path("5").asText());
//
//            map.put(location, rentalFee);
//        }
//
//        rentalFeeMap = Collections.unmodifiableMap(map);
    }


    public ChatCompletionResponseDto getRecommendation(List<String> userSelectedCategory, Double y, Double x) {
        Mono<Map<String, KakaoApiResultDto>> mono = kakaoApiService.getTotalPlaceData(userSelectedCategory, y, x);
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

        return openAiApiService.requestChatCompletionApi(sb.toString());
    }
}
