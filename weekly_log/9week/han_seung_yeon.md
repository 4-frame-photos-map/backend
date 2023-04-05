## 한승연

### 체크리스트(필수)

- security
    - [x]  `fix` : 타도메인 API 호출이 가능하도록 cors 설정 추가(프론트 cors 에러 해결)
- member
    - [x]  `refactor` : 회원정보 조회시 대표칭호가 없는 경우 빈문자열(””)로 반환하도록 수정
    - [x]  `refactor` : 회원이 존재하지 않는 경우 500 대신 404 로 응답하도록 예외처리, GitBook API 수정
    - [x]  `test` : 로그아웃/회원탈퇴시 accessToken 블랙리스트 저장 검증 코드 삭제
    - [ ]  **#1** `fix` : 회원 탈퇴시 Member 삭제하기 전 Member 를 참조하고 있는 엔티티(Review) 삭제하기
- auth
    - [x]  `refactor` : access token 재발급시 refresh token 을 request body 에 담아 요청하도록 수정(프론트 요청)
    - [x]  **#2** 인증이 필요없는 API 에서 유효하지않은 토큰 사용시 오류가 발생하는 문제 해결
        - [x]  `fix` : 인증이 필요한 API 에서만 인증을 처리하기 위해 Filter 대신 Interceptor 로 인가처리(AuthenticationInterceptor)
        - [x]  `fix` : GlobalExceptionHandler 에서 AuthenticationInterceptor 에서 발생하는 JWT 인증 관련 예외처리, JwtExceptionFilter 삭제
    - [x]  서버 부하를 줄이기 위해 accessToken 은 블랙리스트로 관리하지 않도록 수정
        - [x]  `refactor` : 인증처리 과정에서 accessToken 블랙리스트 체크 로직 삭제
        - [x]  `refactor` : 로그아웃/회원탈퇴한 회원의 accessToken을 블랙리스트 저장하는 로직 삭제
    - [x]  `fix` : 인증 관련 에러 응답 statusCode 401로 통일, ErrorCode 유니크하게 수정(프론트 요청)

### 참고자료(선택)

- application-base-addi.yml 운영용 redirect_uri 를 localhost:3000/ 변경
- **#2 [[Spring Boot] Interceptor 를 도입하여 인증이 필요한 API 에서만 인가처리](https://www.notion.so/Spring-Boot-Interceptor-API-f9f3f3e8365a4ea38fcfd7c242820f41)**

### 궁금한점(선택)

- 공통
    - [x]  **#1** 회원이 작성한 리뷰를 삭제하기 위해서 MemberService 가 ReviewService 를 의존하면 MemberSerivce ↔ ReviewService 순환참조 문제 발생함, Member↔Review 양방향 매핑으로 변경해야하는가?
        - 양방향 관계로 바꾸기
        - MemberService 에서 ReviewRepository 의존하기
        - ReviewContoller 에서 MemberService 의존하기 → 채택!
    - [x]  현재 코드에서 포토이즘 스튜디오, 포토이즘 박스, 포토이즘 컬러드 → 포토이즘 브랜드로 분류되는지?
        
        [포토이즘](https://photoism.co.kr/)
        
        - 포토이즘 박스, 포토이즘 컬러드만 포토이즘 브랜드로 응답된다.(포토이즘 스튜디오는 즉석사진으로 검색이 안되어서 DB에 있어도 응답에는 포함되지 않음)
    - [x]  지점 칭호 종류, 부여 기준 설정
- 승근
    - [x]  Gitbook 인증 필요한 API Request Header 에 AccessToken 추가
        
        [MemberTitle API](https://4-cut-photos-map.gitbook.io/api/reference/api-reference/membertitle-api)
        
- 범서
    - [x]  로컬에서 운영 DB connection 은 되는데, select 안되는 문제
        - workbench 8.0.25 버전 다운그레이드

### 다음주 계획(선택)

- member
    - [ ]  **#1** `fix` : 회원 탈퇴시 Member 삭제하기 전 Member 를 참조하고 있는 엔티티(Review) 삭제하기
    - [ ]  `feat` : 성공 응답, 에러 응답 수정(프론트 요청)
    - [ ]  `test` : MemberService 테스트 코드 작성
    - [ ]  `refactor` : 서비스와 함께 로그아웃 구현
- auth
    - [ ]  `fix` : shop 인증처리
    - [ ]  `feat` : 성공 응답, 에러 응답 수정(프론트 요청)
    - [ ]  `refactor` : 인증 인터셉터 코드 리팩토링
    - [ ]  `feat` : 회원가입시 뉴비 칭호 즉시 부여하도록 변경
    - [ ]  `test` : JwtService 테스트 코드 작성
- member-title
    - [ ]  `feat` : 성공 응답, 에러 응답 수정(프론트 요청)
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
- 공통
    - [ ]  `test` : 테스트 코드에 `assertAll()` 적용
