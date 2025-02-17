package com.goorm.friendchise.domain.headquarter.dto.openai;

import lombok.Getter;

@Getter
public enum OpenAiModel {
    GPT_4o_MINI("gpt-4o-mini"),
    GPT_4o("gpt-4o"),
    GPT_o3_MINI("o3-mini");

    private final String value;

    OpenAiModel(String value) {
        this.value = value;
    }
}
