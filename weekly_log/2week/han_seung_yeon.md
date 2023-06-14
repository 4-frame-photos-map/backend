## 한승연

### 체크리스트(필수)

1. 리팩토링
- [x]  RedisDao 주석 달기(개인 공부)
- [x]  MemberController 에서 인증관련 API(로그인, 로그아웃, 토큰 재발급, 회원탈퇴)는 AuthController 로 분리 후 auth 패키지 하위로 이동
- [x]  MemberSerivce 에서 토큰 발급/재발급, 블랙리스트 검증 코드를 JwtService 로 분리
1. 기능 구현 및 수정
- 회원(Member) 기능
    - [x]  로그아웃 기능 수정(로그아웃 요청한 회원의 refreshToken 을 redis 에서 삭제하는 로직 추가)
    - [x]  회원탈퇴 추가기능 구현
        - [x]  kakao accessToken 만료되었는지 검사(kakao 토큰 정보 보기 요청)
        - [x]  토큰이 만료되었을 경우 토큰 갱신 요청 보내기
        - [x]  연결끊기 요청 보내기
- 칭호(Title, TitleLog) 기능
    - [x]  칭호 enum 정의, Title sql 수정, Member Entity 수정(Title 과 양방향 연관관계 설정)
    - [x]  회원가입시 기본 칭호(뉴비) 1개 부여, 대표 칭호 설정하는 로직 추가
    - [ ]  회원 기본정보 페이지에서 대표 칭호 조회
    - [ ]  회원이 갖고 있는 모든 칭호 조회
    - [ ]  기준에 부합하는 칭호 부여

### 수정사항 및 참고자료(선택)

- 로그인 응답 수정(임시)
    1. 기존
    - 우리 서비스에서 사용하는 jwt accessToken, refreshToken 2개만 응답하였다.
    
    ```json
    {
      "code": 200,
      "message": "카카오 로그인 성공, Access Token 발급",
      "result": {
        "accessToken": "...",
        "refreshToken": "..."
      }
    }
    ```
    
    1. 수정
    - 회원탈퇴 요청에서 연결끊기(Kakao API)호출을 위해 jwt Token 과 함께 kakao accessToken, refreshToken 를 응답하였다. 추후 원래대로 되돌리고 카카오 토큰은 세션에서 가져오는 것으로 변경예정
    
    ```json
    {
      "success": true,
      "message": "카카오 로그인 성공(Kakao Token, Jwt Token 발급)",
      "result": {
        "kakaoToken": {
          "accessToken": "...",
          "refreshToken": "..."
        },
        "jwtToken": {
          "accessToken": "...",
          "refreshToken": "..."
        }
      }
    }
    ```
    
- AuthController API url 수정(Postman 테스트 주의)
- application-base-addi.yml 수정(kakao Login redirect-uri 수정, accessToken 재발급 uri 추가)
- restTemplate 에러핸들러 추가(200대 응답이 아닌 400/500 응답이 왔을 때 예외가 터져서 다음 단계로 진행되지 않는 문제 해결)
    - [[Spring 오류해결] HttpClientErrorException$Unauthorized: 401 Unauthorized](https://www.notion.so/Spring-HttpClientErrorException-Unauthorized-401-Unauthorized-8d17e93c89f84d66b4c34443d6c12391)

### 궁금한점(선택)

- MemberController, AuthController 로 분리하고 MemberService, JwtService 로 분리했는데 분리기준을 어떻게 하는게 좋은가?
- 칭호 부여는 해당 기준에 부합하면 바로 부여되도록 설계할 것 인가? 즉, 칭호부여 기준과 관련된 다른 로직 내부에 포함시킬 것인지?? 예를 들어, 리뷰 작성하는 로직 내부에 리뷰를 최초로 작성했는지 검사하고 “리뷰첫걸음” 칭호를 부여하는 로직을 추가하는 식으로 개발하는 것이 맞는지 궁금하다.

### 다음주 계획(선택)

- 회원기능 리팩토링
    - [ ]  회원탈퇴 요청에서 카카오 토큰을 세션에서 가져오는 것으로 로직 변경하기(현재는 postman 으로 테스트되지 않는 문제로 임시로 header 에서 값을 꺼내오고 있음)
    - [ ]  로그인시 카카오 토큰 세션 만료시간 설정하기
- 칭호 기능 구현
    - [ ]  칭호 부여
    - [ ]  회원이 갖고 있는 칭호 조회
