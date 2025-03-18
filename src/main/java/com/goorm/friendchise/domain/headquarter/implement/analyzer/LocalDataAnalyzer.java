package com.goorm.friendchise.domain.headquarter.implement.analyzer;

import reactor.core.publisher.Flux;

import java.util.List;

public interface LocalDataAnalyzer {
    List<String> getLocalDataAnalysis(String localData);
    default Flux<String> getLocalDataAnalysisStream(String localData) {
        throw new UnsupportedOperationException("streaming not supported");
    }
}
