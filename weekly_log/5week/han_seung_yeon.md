## 한승연

### 체크리스트(필수)

- 공통
    - [x]  응답 데이터가 없는 경우 `RsData(success, message, null)` 대신 `RsData(success, message)` 생성자 사용하는 것으로 수정
    - [x]  MemberTitle `ErrorCode` 추가, 서비스 단 예외는 모두 `BusinessException()` 으로 처리
- auth
    - [x]  `restTemplate.postForObject()` 로 JSON 응답을 `KakaoTokenResp`(토큰 받기 API 응답용 DTO)로 매핑해서 가져오도록 수정
    - [x]  발급받은 카카오 토큰을 세션에 저장하던 기존 로직을 Redis, DB에 저장하도록 수정
        - [x]  kakao access token Redis 에 저장하는 것으로 수정
            - key = member:{memberId}:kakao_access_token
            - value = {kakaoAccessToken}
        - [x]  kakao refresh token 을 DB(Member 의 kakaoRefreshToken)에 저장하는 것으로 수정
    - [x]  회원탈퇴시 카카오 토큰을 Redis, DB에서 가져오도록 로직 수정, API 문서 수정
    - [x]  로그인 API 응답시 카카오 토큰을 제외한 JWT 토큰만 응답하도록 수정
    - [x]  `object-type:id` 형태로 Redis 키 설계하는 것으로 리팩토링
        - [x]  Member 의 JWT RefreshToken 관리
            - key = member:{memberId}:jwt_refresh_token
            - value = {jwtRefreshToken}
        - [ ]  로그아웃/탈퇴한 Member 의 JWT AccessToken 블랙리스트 관리
            - key = jwt_black_list:{jwtAccessToken}
            - value = {withdrawl/logout}
- member
    - [x]  회원탈퇴 시 Member 삭제 전 MemberTitleLog, Favorite(Member 를 참조하고 있는 엔티티) 삭제하여 오류해결
    - [x]  회원기본정보 조회 시 대표칭호 발견시 바로 리턴하도록 수정
    - [ ]  테스트 코드 작성
- member-title
    - [x]  회원칭호 정보 조회 API 인증된 사용자만 접근하도록 제한
    - [x]  회원 칭호 부여 구현
        - [x]  `@Scheduled` 로 하는 매일 자정에 실행되도록 설정
        - [x]  일단은 `Spring Batch` 대신 서비스 메서드에서 구현
    - [x]  MemberTitle 엔티티에 `@UniqueConstraint` 로 동일한 (member_id, member_title_id) 가 삽입될 수 없도록 unique 설정
- AWS
    - [x]  프로젝트 빌드 후 jar 파일 만들기(빌드 테스트 실패로 인해 테스트 코드 모두 주석 처리 후 빌드함)
    - [x]  배포(EC2 에 Redis 설치, Spring Boot 서버 띄우기, RDS 사용) → Postman API 호출 테스트 완료

### 참고자료(선택)

- 뱃지 부여 기능 참고
    - [https://github.com/hayeon17kim/TIL/blob/master/project-badge-system.md](https://github.com/hayeon17kim/TIL/blob/master/project-badge-system.md)

### 궁금한점(선택)

- [ ]  인증과정을 거칠때마다 MemberContext 객체를 만들어 SpringSecurity 에 등록해주기 위해 DB 에서 Member 를 조회하는 문제 Redis 사용해서 해결하는게 최선인가?

### 다음주 계획(선택)

- auth
    - [ ]  로그아웃/탈퇴한 Member 의 JWT AccessToken 블랙리스트 관리
        - key = jwt_black_list:{jwtAccessToken}
        - value = {withdrawl/logout}
    - [ ]  인증과정을 거칠때마다 MemberContext 객체를 만들어 SpringSecurity 에 등록해주기 위해 DB 에서 Member 를 조회하는 문제 해결하기
- member-title
    - [ ]  spring-batch 도입할 것인지 자료 검색, 학습
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원

---
