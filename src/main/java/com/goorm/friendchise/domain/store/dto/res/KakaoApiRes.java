package com.goorm.friendchise.domain.store.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoApiRes {

    private List<Document> documents;

    @Getter
    public static class Document {
        private String address_name;
        private String address_type;
        private String x;
        private String y;
        private Address address;
        private RoadAddress road_address; // 도로명 주소 (없을 수도 있음)
    }

    @Getter
    public static class Address {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_3depth_h_name;
        private String x;
        private String y;
    }

    @Getter
    public static class RoadAddress {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String underground_yn;
        private String building_name;
        private String main_building_no;
        private String sub_building_no;
        private String x;
        private String y;
        private String zone_no;
    }

    @Getter
    public static class Meta {
        private boolean is_end;
        private int pageable_count;
        private int total_count;
    }
}
