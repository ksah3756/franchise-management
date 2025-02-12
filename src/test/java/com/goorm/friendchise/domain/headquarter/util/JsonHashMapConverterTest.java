package com.goorm.friendchise.domain.headquarter.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonHashMapConverterTest {

    @Test
    void convertJsonToHashmap() {
        // given, when
        HashMap<String, HashMap<String, List<PlaceData>>> map = JsonHashMapConverter.convertJsonToHashmap("hDongInfo.json");

        // then
        assertThat(map.get("강남구").size()).isEqualTo(13);
        List<PlaceData> placeDataList = map.get("강남구").get("삼성2동");
        assertThat(placeDataList.size()).isEqualTo(2);
        assertThat(placeDataList.get(0).area()).isEqualTo("강남구청역");
        assertThat(placeDataList.get(1).area()).isEqualTo("선정릉역");
    }
}