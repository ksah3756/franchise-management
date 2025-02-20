package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.dto.openai.*;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OpenAiApiService {

    private final WebClient webClient;

    public OpenAiApiService(@Qualifier("openAiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // 예시
    private static final String initialSettingMessage =
            "당신은 프랜차이즈 본사의 매장 관리 서비스 역할을 수행합니다. 주어진 데이터를 활용해, 매장의 입점 추천 점수를 0~100 사이의 정수로 산출하세요."
                    + "결과는 전문적인 어조로 \"{추천/비추천 - [XX점]}, 예상 월 매출: xxx만원\" 형태로 제시하고, 그 근거를 간결하게 설명해야 합니다.\n"
                    + "모든 대괄호 안의 숫자는 선택된 (y, x) 좌표로부터의 거리(m)를 의미합니다. 예: \"반경 1km 내 동일 업종 경쟁 매장: [160, 340, 820]\"은 해당 경쟁 매장이 현재 위치에서 각각 160m, 340m, 820m 떨어져 있음을 뜻합니다.\n"
                    + "응답은 아래와 같은 형식을 반드시 따르세요:\n"
                    + "추천(비추천) - [XX점], 예상 월 매출: xxx만원, m²당 임대료: xxx원\n" +
                    "장점:  \n" +
                    "- (필요한 경우 첫번째 장점)\n" +
                    "- (필요한 경우 두번째 장점)\n" +
                    "- (필요한 경우 세번째 장점)\n" +
                    "단점:  \n" +
                    "- (필요한 경우 첫번째 단점)\n" +
                    "- (필요한 경우 두번째 단점)\n" +
                    "- (필요한 경우 세번째 단점)\n" +
                    "\n" +
                    "서비스가 지원하는 전체 상권의 평균 m²당 임대료는 79135.45원입니다. 다만 임대료가 높은 지역일 수록 일반적으로 유동인구가 많으므로 이를 고려해야 합니다.\n" +
                    "주어진 데이터를 바탕으로 예상 매출과 m²당 임대료를 비교하여 추천 점수를 산출하고, 그 이유를 간결하게 설명하세요.";

    public ChatCompletionResponseDto requestChatCompletion(String data) {
        ChatMessage developerRoleMsg = ChatMessage.of(OpenAiRole.DEVELOPER.getValue(), initialSettingMessage);
        ChatMessage userRoleMsg = ChatMessage.of(OpenAiRole.USER.getValue(), data);
        ChatCompletionRequestDto chatCompletionRequestDto = ChatCompletionRequestDto.of(OpenAiModel.GPT_4o_MINI.getValue(), List.of(developerRoleMsg, userRoleMsg), false);

        return webClient.post()
                .bodyValue(chatCompletionRequestDto)
                .retrieve()
                .bodyToMono(ChatCompletionResponseDto.class)
                .block();
    }

    public Flux<String> requestChatCompletionStream(String data) {
        ChatMessage developerRoleMsg = ChatMessage.of(OpenAiRole.DEVELOPER.getValue(), initialSettingMessage);
        ChatMessage userRoleMsg = ChatMessage.of(OpenAiRole.USER.getValue(), data);
        ChatCompletionRequestDto chatCompletionRequestDto = ChatCompletionRequestDto.of(OpenAiModel.GPT_4o_MINI.getValue(), List.of(developerRoleMsg, userRoleMsg), true);

        return webClient.post()
                .bodyValue(chatCompletionRequestDto)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(ChatCompletionStreamResponseDto.class)
                .onErrorResume(error -> {
                    if (error.getMessage().contains("JsonToken.START_ARRAY")) {
                        return Flux.empty();
                    } else {
                        return Flux.error(error);
                    }
                })
                .filter(response -> {
                    String content = response.choices().get(0).getDelta().getContent();
                    return content != null;
                })
                .map(response -> response.choices().get(0).getDelta().getContent());
    }
}
