## 주민지

### 체크리스트(필수)
- **Refactor** : 키워드로 상점 검색하기
    - [x] **역직렬화**
        - Kakao API 호출 Service 클래스에서 **`Jackson Objectmapper` 이용하여 Json Node로 JSON 중첩배열 역직렬화 처리**
        - Kakao API 응답 DTO(KakaoKeywordResponseDto) **내부클래스 `Document` 제거하여 단순화**
        - Kakao API 응답 DTO(KakaoKeywordResponseDto) **필드명 `Camel case`로 수정**
        - Controller 불필요한 코드 정리
    - [x]  **Shop URI 수정**

-  **Test** : 키워드로 상점 검색하기
    - [x] **독립된 테스트 환경 구현**
        - `@BeforeEach`로 테스트 초기 데이터 생성 및 테이블 모든 row 제거
        - test yml 수정(h2 → mariadb)
    - [x] **Service Test Code**
    - [x] **Controller Test Code**

### 참고자료(선택)

- [Json Node 참고자료 - 1](https://hianna.tistory.com/638)
- [Json Node 참고자료 - 2](https://velog.io/@chlwogur2/%EC%A4%91%EC%B2%A9%EB%90%9C-JSON%EC%9D%84-Java-Object-%EB%A1%9C-%EB%A7%A4%ED%95%91%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%952-With-Jackson)
- [Json Node 참고자료 - 3](https://livenow14.tistory.com/68)
- [MockMvc를 이용한 단위 테스트 - JSON Objects 응답 검증](https://stackoverflow.com/questions/55269036/spring-mockmvc-match-a-collection-of-json-objects-in-any-order)

### 다음주 계획(선택)

- **Test** : 찜 Test Case 구현
- **Refactor** : `SpringBootTest` → `Mockito`로 수정
  - 실제 운영 환경처럼 IOC Container 실행하여 테스트 실행시간 오래걸리는 `SpringBootTest` 대신, 가짜(Mock) 객체 지원하는 테스트 프레임워크 `Mockito`로 리팩토링 예쩡
- **Feat** : 소상공인 공공데이터 `CSV` 파일 읽고 파싱하여 `DB(Mysql)`에 저장 처리
