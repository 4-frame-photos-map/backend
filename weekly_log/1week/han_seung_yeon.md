### 체크리스트(필수)

- [x]  서비스 로그아웃 기능 구현
    - [x]  로그아웃 요청한 회원의 accessToken 블랙리스트로 등록하기
    - [x]  JwtAuthorizationFilter 에서 인증처리할 때 블랙리스트로 등록된 accessToken 인지 검증하는 로직 추가

### 참고자료(선택)

- jwt 로그아웃(Redis 에 accessToken 을 블랙리스트로 관리하기)
    
    [https://github.com/JianChoi-Kor/Login](https://github.com/JianChoi-Kor/Login)
    
    [JWT + Redis Logout 로그아웃 구현하기](https://wildeveloperetrain.tistory.com/61)
    
    [[Spring Security] Logout 처리](https://velog.io/@dailylifecoding/spring-security-logout-feature)
    
    [[Spring Boot, Spring Security, Data JPA] 로그인/로그아웃 2](https://sepang2.tistory.com/84)
    

### 궁금한점(선택)

- `[카카오계정과 함께 로그아웃](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#logout-of-service-and-kakaoaccount)` 으로 서비스 로그아웃을 구현할 경우, 콜백 요청이 들어올 때 `Authorization 헤더` 에 `accessToken` 이 들어있지않아 필터단에서 인증처리를 할 수 없는 문제가 생긴다. 이 방법으로 구현하려면 어떻게 인증처리를 해야하는가?
위 방법에서는 Authorization 헤더가 아닌 `@requestParam(”state”)` 으로 accessToken 값에 접근할 수 있다. 현재는 붕어빵 서비스처럼 카카오계정과 함께 로그아웃은 구현하지 않고 서비스 로그아웃만 하도록 구현하였다.
    1. 카카오계정과 함께 로그아웃 GET 요청을 보낸다.
    2. 성공시 설정된 redirect URI `/member/logout/oauth2/kakao?state={accessToken값}` 로 서비스 로그아웃 요청이 들어온다. 
    3. 필터 단에서 Authorization 헤더의 accessToken 값을 검증하고 인증처리를 진행한다. → 문제 발생

### 다음주 계획(선택)

1. 리팩토링
- MemberController 의 인증관련 API(로그인, 로그아웃)은 AuthController 로 분리하기
- Error 응답 포맷 정리하기
- RedisDao, RedisConfig 주석달기
