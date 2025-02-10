package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.dto.openai.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OpenAiApiService {

    private final WebClient webClient;

    public OpenAiApiService(@Qualifier("openAiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // 예시
    private static final String initialSettingMessage =
            "당신은 프랜차이즈 본사의 매장 관리 서비스의 기능으로서 매장 입점 추천 점수를 0~100점 사이의 정수로 알려줘야 합니다."
            + "전문성 있는 말투로 주어진 데이터를 활용하여 추천/비추천 여부를 먼저 말하고, 그 근거를 간결하게 설명하시오."
            + "모든 데이터는 현재 선택된 y, x 좌표로부터 몇 m 떨어진 위치에 존재한다는 것을 의미합니다."
            + "ex. 반경 1km 내 동일 업종 경쟁 매장: [160, 340, 820] 이면 1km 반경 내 동일 업종 경쟁 매장이 현재 위치로부터 160m, 340m, 820m 떨어진 위치에 존재하는 것을 의미합니다.";

    public ChatCompletionResponseDto requestChatCompletionApi(String data) {
        ChatMessage developerRoleMsg = ChatMessage.of(OpenAiRole.DEVELOPER.getValue(), initialSettingMessage);
        ChatMessage userRoleMsg = ChatMessage.of(OpenAiRole.USER.getValue(), data);
        ChatCompletionRequestDto chatCompletionRequestDto = ChatCompletionRequestDto.of(OpenAiModel.GPT_4o_MINI.getValue(), List.of(developerRoleMsg, userRoleMsg));
        return webClient.post()
                .bodyValue(chatCompletionRequestDto)
                .retrieve()
                .bodyToMono(ChatCompletionResponseDto.class)
                .block();
    }
}
