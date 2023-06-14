## 박범서

### 체크리스트(필수)

- Shop **브랜드 검색**
    - [x]  브랜드 API 작성
    - [x]  거리 순 정렬
- **Shop 마커 표시 (하루필름, 포토이즘박스, 인생네컷, 픽닷)**
    - [x]  카카오 맵 API를 활용하여 주변 반경 2km 이내 "브랜드 별 Shop" 가져오기 (RestTemplate)
    - [x]  카카오 api, DB 비교

### 참고자료(선택)

- 카카오 api 검색 시, 최대 15개까지 밖에 안됨
    - 페이지 하나 당, size : 15 → page 3번까지 가능
    - 최대 45건 이용가능
    - 4번하면 중복데이터 포함       
  - [카카오 로컬 api 키워드로 장소 검색 size 크기 문의](https://devtalk.kakao.com/t/api-size/121352)

  - [키워드로 장소 검색하기 API 문서 오류 확인 바랍니다](https://devtalk.kakao.com/t/api/123543)

### 궁금한점(선택)

- ShopController ResponseEntity를 RsData로 할건지
  - 회의결과: ResponseEntity로 감싸는거로 결정
- Shop Entity 필드 == 카카오 맵 Api Response
  - 회의결과 : 혼란 방지를 위해, 일치시키기로 결정

### 다음주 계획(선택)
- Shop 리팩토링 (Shop API 반환값 : RsData -> ResponseEntity, distance 포멧)
- ShopTitle
- KaKao 맵 API 결과 Jackson 처리
