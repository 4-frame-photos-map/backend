## 한승연

### 체크리스트(필수)

- global
    - [x]  `feat` : 파일 업로드 예외처리 추가(잘못된 확장자, 파일 용량 초과)
- aws
    - [x]  aws s3 bucket 생성/권한 설정, 사용자 생성(access-key)
    - [x]  브랜드 대표 이미지 s3 버킷에 업로드, DB 에 반영
- file
    - [x]  파일 업로드 기능
        - [x]  `chore` : aws s3 파일 업로드를 위한 의존성 추가
        - [x]  `feat` : s3 파일 업로드 설정 파일 추가
        - [x]  `feat` : 다중 이미지 파일 업로드 API 개발
        - [ ]  `feat` : 파일 리사이즈
- auth
    - [x]  #1 `fix` : 고정값대신 프론트 로컬(localhost:3000), 운영 경우에 따른 redirect-uri 를 사용하도록 수정
    - [x]  `refactor` : 운영 yml 파일(application-base-addi.yml)에서 redirect-uri, jwt secret-key 수정, 사용하지 않는 값(reissue-uri, header) 삭제(Actions secret, notion 반영)
    - [x]  `fix` : WebConfig 에 리뷰 단건 조회, 지점 전체 리뷰 조회 API 인증하지 않도록 인터셉터 제외 설정(인증이 필요없는 API 에서 인증 처리를 하는 문제 해결)
- config
    - [x]  `feat` : WebConfig 에 프론트 도메인 origin 허용 설정

---

### 참고자료(선택)

- jwt secret key 발급
    
    [jwt secret key 만들기](https://blossoming-man.tistory.com/entry/jwt-secret-key-만들기)
    

`#1 문제`

- 프론트가 로컬(localhost:3000)에서 요청할 때 사용되는 redirect-uri 와 운영(photosmap.vercel.app)에서 요청할 때 사용되는 redirect-uri 가 다르다.
- kakao login redirect-uri
    - 프론트 로컬 개발할 때 API 요청: http://localhost:3000/auth/kakao
    - 운영할 때 API 요청 : https://photosmap.vercel.app/auth/kakao
1. 기존
- yml 파일에서 설정한 고정된 값을 redirect-uri 로 사용함
    
    ```java
    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectURI;
    ```
    
    - yml 파일로부터 고정된 redirect-uri 를 사용하면 2가지 중 1가지 경우를 처리할 수 없는 문제가 생긴다.
        - redirect-uri 를 http://localhost:3000/auth/kakao 로 설정할 경우 : 운영에서 오는 API 요청 처리 불가
        - redirect-uri 를 https://photosmap.vercel.app/auth/kakao 로 설정할 경우 : 로컬에서 오는 API 요청 처리 불가
1. 변경
- 카카오 로그인 리다이렉트 요청이 들어온 request 의 Origin 값을 활용함
    
    ```java
    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectURI;
    
    // 요청 origin 에 따른 redirect-uri 조회
    public String getRedirectURI(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin + redirectURI;
    }
    ```
    
    - Origin 값에 따라 redirect-uri 를 만들어 사용한다.
    - 로컬에서 온 요청이면 = Origin 이 http://localhost:3000 이면 → redirect-uri 는 http://localhost:3000/auth/kakao
    - 운영에서 온 요청이면 = Origin 이 https://photosmap.vercel.app 이면 → redirect-uri 는 https://photosmap.vercel.app/auth/kakao

### 궁금한점(선택)

### 다음주 계획(선택)

- file
    - [ ]  `feat` : 파일 리사이징
- auth
    - [ ]  `refactor` : RestTemplate 대신 WebClient 사용
    - [ ]  `refactor` : 서비스와 함께 로그아웃 구현
    - [ ]  `test` : JwtService 테스트 코드 작성
- member
    - [ ]  `refactor` : 닉네임으로 조회할 일이 많으니 index 설정
    - [ ]  `refactor` : 회원탈퇴시 delete 쿼리가 1개씩 날라가는 문제 해결
    - [ ]  `test` : MemberService 테스트 코드 작성
- member-title
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
- test
    - [ ]  `test` : 테스트 코드 계층구조, assertAll() 적용
