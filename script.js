import http from "k6/http";
import { check, sleep } from "k6";
import exec from "k6/execution";

export let options = {
    stages: [
        { duration: "1m", target: 25 }, // 1분 동안 VUser를 25명까지 증가
        { duration: "2m", target: 150 },
        { duration: "2m", target: 420 },
        { duration: "1m", target: 0 }, // 1분 동안 VUser를 0명으로 감소
    ],
};

const BASE_URL =
    "http://localhost:8080"; // 테스트 대상 서버 URL로 변경 (예: http://localhost:8080)

export default function () {
    // 1. 로그인하여 토큰 받기
    const loginUrl = BASE_URL + "/manager/login";
    const loginPayload = JSON.stringify({
        username: "hq_manager_" + exec.vu.idInInstance, // 실제 로그인 사용자명으로 변경
        password: "1234", // 실제 로그인 비밀번호로 변경
    });
    const loginParams = {
        headers: { "Content-Type": "application/json" },
        tags: {
            page_name: "login",
        },
    };

    let loginRes = http.post(loginUrl, loginPayload, loginParams);
    check(loginRes, {
        "login status is 200": (r) => r.status === 200,
    });

    sleep(1);

    // 로그인 응답에서 토큰 추출 (예: 응답 JSON에 token 필드가 있다고 가정)
    let accessToken = loginRes.json().accessToken;
    let refreshToken = loginRes.json().refreshToken;
    if (!accessToken) {
        // 토큰이 없는 경우 테스트를 실패 처리
        console.error("토큰이 반환되지 않았습니다.");
        return;
    }

    // 2. /headquarter 엔드포인트에 GET 요청 (토큰 포함)
    const hqUrl = BASE_URL + "/headquarter";

    const hqParams = {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        tags: {
            page_name: "headquarter",
        },
    };

    let hqRes = http.get(hqUrl, hqParams);
    check(hqRes, {
        "headquarter GET status is 200": (r) => r.status === 200,
    });

    sleep(2);

    // 3. /headquarter/store-recommendation-stream-dummy 엔드포인트에 POST 요청 (토큰 포함)
    const recommendationUrl =
        BASE_URL + "/headquarter/store-recommendation-stream-dummy";
    const recommendationPayload = JSON.stringify({
        user_selected_category: ["학교", "주차장"],
        x: 125.0,
        y: 35.0,
    });
    const recommendationParams = {
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`,
        },
        tags: {
            page_name: "store-recommendation",
        },
    };

    let recRes = http.post(
        recommendationUrl,
        recommendationPayload,
        recommendationParams
    );
    check(recRes, {
        "recommendation POST status is 200": (r) => r.status === 200,
    });

    sleep(1);
}
