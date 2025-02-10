package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAiApiServiceTest {

    @Autowired
    private OpenAiApiService openAiApiService;

    @Test
    void requestChatCompletionApi() {
        ChatCompletionResponseDto chatCompletionResponseDto = openAiApiService.requestChatCompletionApi("패스트푸드 업종" +
                "임대료 500만원, 반경 lkm 내 주변 경쟁사 3개, 반경 200m 내 버스 정류장 1개, 반경 500m 내 지하철역 1개, 반경 1km 내 학교 1개");
        System.out.println(chatCompletionResponseDto);
    }
}