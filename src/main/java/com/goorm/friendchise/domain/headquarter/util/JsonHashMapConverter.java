package com.goorm.friendchise.domain.headquarter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class JsonHashMapConverter {
    public static HashMap<String, HashMap<String, List<PlaceData>>> convertJsonToHashmap(String fileName) {
        try {
            ClassLoader classLoader = JsonHashMapConverter.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("JSON 파일을 찾을 수 없습니다: " + fileName);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream,
                    new TypeReference<HashMap<String, HashMap<String, List<PlaceData>>>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 파일을 파싱하는데 실패했습니다: " + fileName);
        }
    }
}
