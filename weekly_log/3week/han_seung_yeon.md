## 한승연

### 체크리스트(필수)

- auth
    - [x]  [auth API 문서 작성(인증관련 에러 포함)](https://www.notion.so/auth-API-0fb0e74234434ae7983dc87356c0addf)
- member
    - [x]  회원 기본 정보 조회 응답에 보유 칭호 개수 추가
    - [x]  [member API 문서 작성](https://www.notion.so/member-API-a9806c3529234faa9079bd6b0fc48772)
- member-titles
    - [x]  전체 회원 칭호 목록 조회
    - [x]  회원 칭호 정보 조회
    - [x]  [member-titles API 문서 작성](https://www.notion.so/member-titles-API-be8fbae3ab9c47e5aedc377ba993f0d7)

### 참고자료(선택)

- REST API 문서 작성
    
    [Kakao Developers](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)
    
    [📜 REST API Reference](https://github.com/tTab1204/Cooking_project/wiki/%F0%9F%93%9C-REST-API-Reference#%EC%A0%84%EC%B2%B4-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-post-eventsshow-events)
    
    [Kakao Developers](https://developers.kakao.com/docs/latest/ko/kakaologin/trouble-shooting)
    

### 궁금한점(선택)

- [x]  data.sql 이 하는 역할은 무엇인가?
    - data.sql 은 스프링부트 실행시 **초기 데이터를 삽입**해주는 역할을 한다.
    - `resources/data.sql` 에 초기 데이터로 삽입할 SQL 쿼리를 작성한다.
    - `application.yml` 에 아래와 같이 설정을 추가한다.
        - `spring.sql.init.mode: always`
        - `spring.jpa.defer-datasource-initialization: true`
    
    [스프링부트 실행 시, Database sql 데이터 삽입](https://velog.io/@jupiter-j/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-%EC%8B%A4%ED%96%89-%EC%8B%9C-Database-sql-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%82%BD%EC%9E%85)
    
- [x]  회원탈퇴 API 의 URI `GET /auth/withdrawl` 가 REST API URI 규칙에 맞지 않아서 수정해야하지 않을까?
    - REST API URI 규칙에 따라 동사를 제거하고 HTTP METHOD 로 행위를 대신한다.
    - 결론적으로 `DELETE /member` 로 바꾸기로 했다. 회원탈퇴는 인증(auth)쪽보다는 회원 관련 기능인 것 같아 수정했다.
    
    [REST API URI 규칙](https://velog.io/@pjh612/REST-API-URI-%EA%B7%9C%EC%B9%99)
    
- [x]  MemberTitleLog 를 조회할 때 MemberTitleLog 에서 조회할 것 인가? Member 에 양방향 매핑을 설정해서 Member 에서 조회할 것인가?
    - 직접 2가지 방식을 테스트해보고 글을 작성하였습니다.
    
    [단방향 매핑 vs 양방향 매핑](https://www.notion.so/vs-4b246ccf6121494cb0206e5365d0ca09)
    

### 다음주 계획(선택)

---

- member
    - [ ]  닉네임 수정 기능 구현
    - [ ]  REST API 에 맞게 URI 수정
    - [ ]  회원탈퇴 요청에서 카카오 토큰을 세션에서 가져오는 것으로 로직 변경하기(현재는 postman 으로 테스트되지 않는 문제로 임시로 header 에서 값을 꺼내오고 있음)
    - [ ]  로그인시 카카오 토큰 세션 만료시간 설정하기(refresh token 의 유효기간만큼 설정하면되는건지?)
- auth
    - [ ]  REST API 에 맞게 URI 수정
