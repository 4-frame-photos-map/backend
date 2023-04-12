## 한승연

### 체크리스트(필수)

- global
    - [x]  `refactor` : 에러 응답 포맷 수정(프론트 요청)
    - [x]  `feat` : 잘못된 Request Body JSON 형식에 대한 예외처리
- member
    - [x]  `refactor` : 성공 응답 포맷 수정(프론트 요청), Gitbook 반영
    - [x]  `refactor` : 회원가입/로그인 로직 리팩토링
    - [x]  `refactor` : 로그아웃/회원탈퇴시 Redis 에서 kakaoAccessToken 삭제하기
    - [x]  `test` : MemberService 테스트 코드 수정
    - [x]  **#1** `fix` : 회원 탈퇴시 Member 삭제하기 전 Member 를 참조하고 있는 엔티티(Review) 삭제하기(MemberService 만 ReviewService 의존)
    - [x]  `feat` : 회원가입시 뉴비 칭호 즉시 부여하도록 변경
    - [x]  `feat` : 닉네임 중복체크 API 개발(프론트 요청)
    - [x]  `feat` : 닉네임 수정 API 닉네임 네이밍 규칙(특수문자 제외 2~10자리) 검증 추가
    - [x]  `fix` : 닉네임이 null 일 때 수정되는 문제 해결(@NotNull 추가)
- auth
    - [x]  `refactor` : 토큰 관련 기능(토큰 꺼내기, 토큰타입 검사) JwtProvider 메서드로 수정
    - [x]  “shops/**” 패턴의 모든 API 호출시 인증 처리를 하지 않는 문제 해결
        - [x]  `fix` : 선택적 인증 처리를 위한 OptionalAuthenticationInterceptor 추가, 설정
            - Authorization Header 에 값이 있는 경우 → 인증처리O
            - Authorization Header 에 값이 없는 경우 → 인증처리X
    - [x]  `fix` : /auth/login/kakao 호출시 /error 로 리다이렉트 될 때 인터셉터 preHandle 실행되는 문제 해결
    - [x]  `refactor` : 성공 응답 포맷 수정(프론트 요청), Gitbook 반영
- member-title
    - [x]  `refactor` : 성공 응답 포맷 수정(프론트 요청), Gitbook 반영

---

### 참고자료(선택)

### 궁금한점(선택)

### 다음주 계획(선택)

- global
    - [ ]  `feat` : 파일 업로드 예외처리 추가
- file
    - [ ]  aws s3 bucket 생성, 사용자 생성(access-key)
    - [ ]  파일 업로드 기능
        - [ ]  `chore` : aws s3 파일 업로드를 위한 의존성 추가
        - [ ]  `feat` : s3 파일 업로드 설정 파일 추가
        - [ ]  `feat` : 파일 업로드 API 개발, 예외처리
        - [ ]  `feat` : 파일 리사이즈
        - [ ]  `feat` : 다수 파일 업로드
- auth
    - [ ]  `refactor` : 서비스와 함께 로그아웃 구현
    - [ ]  `test` : JwtService 테스트 코드 작성
- member
    - [ ]  `refactor` : 닉네임으로 조회할 일이 많으니 index 설정
    - [ ]  `refactor` : 회원탈퇴시 delete 쿼리가 1개씩 날라가는 문제 해결
    - [ ]  `test` : MemberService 테스트 코드 작성
- member-title
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
- 테스트
    - [ ]  `test` : 테스트 코드 계층구조, assertAll() 적용
