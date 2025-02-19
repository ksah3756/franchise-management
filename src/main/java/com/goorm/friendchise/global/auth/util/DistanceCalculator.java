package com.goorm.friendchise.global.auth.util;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (킬로미터 단위)

    /**
     * 두 지점 사이의 거리를 계산합니다.
     *
     * @param y1 첫 번째 지점의 위도
     * @param x1 첫 번째 지점의 경도
     * @param y2 두 번째 지점의 위도
     * @param x2 두 번째 지점의 경도
     * @return 두 지점 사이의 거리 (킬로미터 단위)
     */
    public static double calculateDistance(double y1, double x1, double y2, double x2) {
        // 위도와 경도를 라디안 단위로 변환
        double lat1Rad = Math.toRadians(y1);
        double lon1Rad = Math.toRadians(x1);
        double lat2Rad = Math.toRadians(y2);
        double lon2Rad = Math.toRadians(x2);

        // 하버사인 공식 적용
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

           double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2); // 수정된 부분

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 최종 거리 계산
        return EARTH_RADIUS_KM * c;
    }
}
