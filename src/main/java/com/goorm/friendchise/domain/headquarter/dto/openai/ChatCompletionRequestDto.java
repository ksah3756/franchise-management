package com.goorm.friendchise.domain.headquarter.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

public record ChatCompletionRequestDto(
        String model,
        List<ChatMessage> messages
){

    public static ChatCompletionRequestDto of(String model, List<ChatMessage> chatMessages) {
        return new ChatCompletionRequestDto(model, chatMessages);
    }
}
