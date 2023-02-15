## 주민지

### 체크리스트(필수)

- [x]  키워드로 Shop 검색 리팩토링
    - [x]  ObjectMapper 이용하여 Kakao API 호출 Service 클래스에서 역직렬화까지 처리
    - [x]  불필요한 Controller 코드 정리
    - [x]  Shop URI 수정
    - [x]  Kakao 응답 DTO 필드명 Camel case로 수정
- [x]  키워드로 Shop 검색 Test Case 구현
    - [x]  @BeforeEach로 독립된 테스트 환경 만들기
        - [x]  테스트 초기 데이터 생성
        - [x]  test yml 수정(h2 → mariadb)
    - [x]  Service Test Code
    - [ ]  Controller Test Code

### 참고자료(선택)

- [Json Node 참고자료 - 1](https://hianna.tistory.com/638)
- [Json Node 참고자료 - 2](https://velog.io/@chlwogur2/%EC%A4%91%EC%B2%A9%EB%90%9C-JSON%EC%9D%84-Java-Object-%EB%A1%9C-%EB%A7%A4%ED%95%91%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%952-With-Jackson)
- [Json Node 참고자료 - 3](https://livenow14.tistory.com/68)

### 다음주 계획(선택)

- 찜 Test Case 구현
- `SpringBootTest` → `Mockito`로 리팩토링
  - 실제 운영 환경처럼 IOC Container 실행하여 테스트 실행시간 오래걸리는 `SpringBootTest` 대신, 가짜(Mock) 객체 지원하는 테스트 프레임워크 `Mockito` 사용
- 소상공인 공공데이터 `CSV` 파일 읽고 파싱하여 `DB(Mysql)`에 저장 처리
