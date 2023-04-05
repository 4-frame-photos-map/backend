## 최승근

### 체크리스트

- Gitbook
    - [x]  회원 칭호 (MemberTitle API)
    - [x]  지점 저장(Favorites API)
    - [x]  상점 칭호 (ShopTitleLogs API)
- 브랜드(brand ) 기능
    - [x]  Brand Entity 생성, 연관 관계 설정
    - [x]  브랜드 정보 조회 로직 생성하기
    - [x]  Shop 내부 Brand 수정
        - 브랜드 없을 경우 : 기타 설정
        - 브랜드 정보가 있는 경우 : 해당 Brand 설정(교체)
    - [x]  브랜드 정보 필요한 API 응답에 brand DTO 함께 응답하도록 수정
        - ResponseBrandDto 생성
        - 브랜드 정보 필요한 응답(ResponseShop)에 ResponseBrandDto 추가하기
            - 키워드 검색 /shops
            - 브랜드 검색 /shops/brand
- 리뷰(Review) 리팩터링
    - [ ]  회원 전체 리뷰 요청 시 상점 정보를 포함한 응답 생성
    - [ ]  리뷰 작성 요청 형식 변경하기
    - [ ]  청결, 보정, 소품 관련 통계 로직 생성

### 궁금한 점

1. 대표 브랜드 4개와 기타 브랜드로 처리하기로 했었는데 일반 브랜드 정보(픽닷, 포토스트리트 등)를 DB에 저장 여부
    
    ⇒ 회의결과 : Brand Table 내부에 총 5개 브랜드만 저장[대표 브랜드 4, 기타 브랜드 1]
    
2. 지점(Shop)은 코드 내에서 new를 통해서 생성되지 않고, 공공 데이터를 추가 시켜서 생성되기 때문에 Shop 의 브랜드를 지정하는 로직 처리 방법
    
    ⇒ 회의 결과 : 데이터베이스 쿼리를 이용해서 직접 Shop.brand_id 수정
    

### 다음 주 계획

- 전체 회의 결과 변경 사항을 반영한 리뷰 관련 리팩토링
    - 회원 전체 리뷰 조회, 리뷰 조회 응답 DTO 분리하기
        - Shop 정보를 가진 응답
        - Shop 정보를 제외한 응답
    - 리뷰 작성 요청 형식 변경에 따른 요청 데이터 처리 로직 수정
    - 청결, 보정, 소품 관련 통계 로직 작성
- 응답 메세지(ResponseEntity) 변경에 따른 응답 메세지 형식 수정
- 요청, 응답 수정 내용 바탕으로 Gitbook 변경
- 운영 DB 내부 brand Table 생성, shop Table 업데이트, shop.brand_id 컬럼 수정
