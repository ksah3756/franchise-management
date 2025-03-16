## 입지 분석 기능


<img width="831" alt="스크린샷 2025-03-17 오전 12 56 30" src="https://github.com/user-attachments/assets/ca6ddd35-4808-4e87-af91-612f89458329" />

[2023년 서울시 상가임대차 실태조사](https://sftc.seoul.go.kr/fe/bbs/NR_view.do?bbsCd=2&bbsSeq=767&currentPage=1&rowPerPage=10&ctgCd=+&searchKey=&searchVal=)의 주요 상권 별 임대료 데이터와 [서울 열린 데이터 광장](https://data.seoul.go.kr/dataList/OA-15560/S/1/datasetView.do)의 상권 영역 shp 파일을 활용
> 지원하는 상권 영역 내 위치 선택 가능

<img width="820" alt="스크린샷 2025-03-17 오전 12 56 45" src="https://github.com/user-attachments/assets/929d0a3e-634a-44bc-a5b1-197805be500f" />

- 사용자 지정 위경도로부터 반경 500m 내 동일 프랜차이즈 매장이 있을 경우 분석 X
- 반경 1km 내 유사 업종 매장 수, 반경 200m 내 버스 정류장 수, 반경 500m 내 지하철역 수 (필수)
- 반경 500m 내 대형마트, 학교, 주차장, 문화시설, 관광명소, 음식점, 카페, 숙박, 병원, 약국 수 (선택)

위 데이터를 바탕으로 매장 입지 분석 보고서 제공
> 카카오맵 API, OpenAi API 활용

![Untitled diagram-2025-02-20-044548 (1)](https://github.com/user-attachments/assets/f24d272a-74ed-4566-9b38-1d5207d731d2)

---

