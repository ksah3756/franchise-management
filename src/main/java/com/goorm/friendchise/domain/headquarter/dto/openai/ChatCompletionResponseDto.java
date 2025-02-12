package com.goorm.friendchise.domain.headquarter.dto.openai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

public record ChatCompletionResponseDto(
        List<Choice> choices,
        Usage usage
) {
    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Choice {
        private final ChatMessage message;

        public static Choice of(ChatMessage message) {
            return new Choice(message);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Usage {
        private final int promptTokens;
        private final int completionTokens;
    }

    public static ChatCompletionResponseDto of(List<Choice> choices, Usage usage) {
        return new ChatCompletionResponseDto(choices, usage);
    }
}
