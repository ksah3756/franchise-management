package com.goorm.friendchise.domain.headquarter.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
