## 요식업 프랜차이즈 본사의 매장 입점 여부 결정을 도와주기 위한 입지 분석 기능 (서울특별시 내)

### Demo
---

![매장입지분석요약4 (2)](https://github.com/user-attachments/assets/366fd879-ebcc-482a-99ab-9eeeb05f325a)

### How
---

분석에 사용되는 입지 요소

1. 주변 유사 업종 매장
2. 주변 버스 정류장, 지하철역, 대형마트, 학교, 문화시설 등 인프라 
3. 해당 상권의 유동인구 (추가 예정)


<img width="831" alt="스크린샷 2025-03-17 오전 12 56 30" src="https://github.com/user-attachments/assets/ca6ddd35-4808-4e87-af91-612f89458329" />

[2023년 서울시 상가임대차 실태조사](https://sftc.seoul.go.kr/fe/bbs/NR_view.do?bbsCd=2&bbsSeq=767&currentPage=1&rowPerPage=10&ctgCd=+&searchKey=&searchVal=)의 주요 상권 별 임대료 데이터와 [서울 열린 데이터 광장](https://data.seoul.go.kr/dataList/OA-15560/S/1/datasetView.do)의 상권 영역 shp 파일을 활용하였습니다.

> 선정된 주요 상권 110곳의 [geojson](https://github.com/ksah3756/franchise-management/blob/dev/commercial_area.geojson?short_path=cd9e391) 과 [sql](https://github.com/ksah3756/franchise-management/blob/dev/commercial_area_data.sql) 파일을 다운로드하실 수 있습니다. (WGS 84(EPSG 4326) 기준)

<img width="820" alt="스크린샷 2025-03-17 오전 12 56 45" src="https://github.com/user-attachments/assets/929d0a3e-634a-44bc-a5b1-197805be500f" />

- 사용자 지정 위경도로부터 반경 500m 내 동일 프랜차이즈 매장이 있을 경우 분석 X
- 반경 1km 내 유사 업종 매장 수, 반경 200m 내 버스 정류장 수, 반경 500m 내 지하철역 수 (필수)
- 반경 500m 내 대형마트, 학교, 주차장, 문화시설, 관광명소, 음식점, 카페, 숙박, 병원, 약국 수 (선택)

위 데이터를 바탕으로 매장 입지 분석 보고서를 제공합니다.
> 카카오맵 API, OpenAi API 활용

### Sequence Diagram
---

![Untitled diagram-2025-02-20-044548 (1)](https://github.com/user-attachments/assets/f24d272a-74ed-4566-9b38-1d5207d731d2)


### Performance Improvement
---


<img width="285" alt="스크린샷 2025-03-23 오후 9 33 18" src="https://github.com/user-attachments/assets/b97fe09c-f588-425d-8b3b-2317d8f6e937" />

**1127ms -> 143ms**

동기적으로 실행될 필요가 없는 API 호출들을 논블로킹 방식으로 전환하여 블로킹 방식에 비해 전체 주변 인프라 데이터를 받아오는 시간을 **87%** 감소





