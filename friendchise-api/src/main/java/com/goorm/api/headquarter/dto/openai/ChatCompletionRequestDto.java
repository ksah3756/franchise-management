package com.goorm.api.headquarter.dto.openai;

import java.util.List;

public record ChatCompletionRequestDto(
        String model,
        List<ChatMessage> messages,
        double temperature,
        boolean stream
){

    public static ChatCompletionRequestDto of(String model, List<ChatMessage> chatMessages, boolean stream) {
        return new ChatCompletionRequestDto(model, chatMessages, 0, stream);
    }
}
