package com.goorm.friendchise.domain.headquarter.dto.openai;

import java.util.List;

public record PlaceSummaryDto(
        int totalCount, // 데이터 개수
        List<String> distances
) {
    public static PlaceSummaryDto of(int totalCount, List<String> distances) {
        return new PlaceSummaryDto(totalCount, distances);
    }
}
