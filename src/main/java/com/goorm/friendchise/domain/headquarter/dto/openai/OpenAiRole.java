package com.goorm.friendchise.domain.headquarter.dto.openai;

import lombok.Getter;

@Getter
public enum OpenAiRole {
    DEVELOPER("developer"),
    USER("user");

    private final String value;

    OpenAiRole(String value) {
        this.value = value;
    }
}
