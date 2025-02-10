package com.goorm.friendchise.domain.headquarter.dto.openai;

public record ChatMessage(
        String role,
        String content
) {
    public static ChatMessage of(String role, String content) {
        return new ChatMessage(role, content);
    }
}
