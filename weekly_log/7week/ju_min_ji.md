## 주민지

### 체크리스트(필수)
- **feat** : Shop 초기데이터 생성
    - [x] 카카오 API데이터와 DB Shop 데이터 비교로직 수정
        - 장소명 비교에서 도로명주소 비교로 수정
            - **키워드 검색은 중복 데이터 발생 시 장소명으로 2차 필터링**
            - **브랜드 검색, 현재위치 마커는 중복 데이터 발생 시 첫번째 데이터만 반환하고 break**
              - 키워드 검색과 달리 브랜드 검색, 현재위치 마커는 브랜드 포함여부로 1차적인 데이터 비교를 거쳤기 때문
    - [x] DB `brand` 컬럼 제거 후 브랜드 검색시 `findByBrand` 대신 `findDistinctByPlaceNameStartingWith` 메서드로 수정
- **refactor** : 키워드로 Shop 검색 거리순 정렬 추가
   - [x] 응답 DTO에 distance 추가
   - [x] 기존 거리포맷팅(1km 단위만 소수점 첫째자리 표시)에서 **1km, 10km 단위는 소수점 첫째자리 표시**되도록 수정
- **feat** : 회원 찜 목록 정렬
   - [x] 정렬 파라미터 추가하여 `최근추가순`, `장소명순`으로 **API 분리 및 정렬 구현** 
     
     |최근추가순(디폴트)|장소명순|
     |--------------|------|
     |`/favorites` |`/favorites?sort=placename` |
- **test** 
   - [x] Shop 테스트 수정 사항들 반영
   - [x] Favorite Service 테스트 추가

### 궁금한 점(선택)
Q1. 키워드 검색 API vs DB 데이터 비교로직에서 장소명으로 2차 필터링 시, 장소명 일치여부(equals)가 아니라 포함관계(contains)로 비교하도록 구현했는데 어떻게 생각하는지
  - 1차 : 도로명 주소 비교
  - 2차 : 카카오 API 데이터 장소명이 DB 장소명을 포함하는지 비교
  - 위와 같은 로직을 구현한 이유 : 장소명에 따로 지점명 없이 브랜드명만 있는 DB 데이터(공공데이터)가 다수 존재하기 때문
  - EX. 1차로 도로명 주소 검사는 거쳤다고 했을 때, `카카오 API placeName = 하루필름 망원점` vs `DB placeName = 하루필름`
  
  - `회의 결과`: 불안정한 초기데이터 문제(완전하지 않은 장소명)라는 근본적인 문제 해결 필요
    - 백에서 이전 페이지 파라미터를 저장하는 방법(httpservletrequest)도 생각해보았으나 REST API로 구현하여 이전에 호출한 API 기록이 남을 수 있지 모르겠고, 카카오 API 데이터를 캐싱할 시 링크 공유받은 사용자 문제발생 가능
    - 따라서 추가적으로 필요한 페이지에 카카오 API를 호출하는 방법 생각해봐야 할 것 같다는 결론
 
Q2. MemberContext로 인증이 필요한 Spring Test 방법
  - 단순히 Member 생성만이 아니라, 로그인 된 채로 해당 Member의 MemberContext(즉 스프링시큐티리 인증정보 등록 후 인증 객체 가져오기)까지 필요할 때 Test 구현하는 방법
  - 시도방법 1: [Spring Security(스프링 시큐리티)를 사용하는 경우 단위테스트](https://reiphiel.tistory.com/entry/spring-security-unit-test)
  - 시도방법 2 : [Spring Boot | Spring Security Test @WithMockUser를 커스터마이징 해서 사용하자](https://gaemi606.tistory.com/entry/Spring-Boot-Spring-Security-Test-WithMockUser%EB%A5%BC-%EC%BB%A4%EC%8A%A4%ED%84%B0%EB%A7%88%EC%9D%B4%EC%A7%95-%ED%95%B4%EC%84%9C-%EC%82%AC%EC%9A%A9%ED%95%98%EC%9E%90)
### 다음주 계획(선택)
- refactor : Shop 브랜드검색, 현재위치 기준 검색
  - 현재위치 기준 Map Marker 
    - 수정 전 : 브랜드별로 분기
    - 수정 후 : 전체
  - 브랜드검색 Map Marker
    - 수정 전: 리스트형, 반경 2km 이내 미적용
    - 수정 후: 맵 마커형, 반경 2km 이내 적용
  - 카카오 API 호출 로직 중복 제거(키워드검색, 브랜드검색, 현재위치기준검색)
