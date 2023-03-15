## 한승연

### 체크리스트(필수)

- global
    - [x]  DatabaseCleaner 테스트 코드 작성
        - [x]  EntityManger 사용 테스트(엔티티 타입, 엔티티명, 테이블명 조회하기)
- member
    - [x]  테스트 코드 작성
        - [x]  기존회원, 새로운 회원 로그인 테스트
        - [x]  회원 탈퇴 테스트
        - [x]  회원 닉네임 수정 테스트
        - [x]  회원 대표 칭호 설정 테스트
    - [x]  회원 탈퇴 시 redis 에서 회원의 jwtRefreshToken 삭제 안되는 문제 해결
        - Redis 에 `jwtRefreshToken` 을 저장할 때 key 이름을 `{memberId}` → `member:{memberId}:jwt_refresh_token` 으로 바꾼 것을 반영함
    - [x]  회원 대표 칭호 설정 로직 리팩토링
- member-title
    - [x]  찜 홀릭, 리뷰 첫 걸음, 리뷰 홀릭 칭호 부여 테스트 코드 작성

### 참고자료(선택)

### 궁금한점(선택)

- member
    - [x]  로그인 로직 고민
        1. `AuthController` 에서 `getMember()`, `generateTokens()` 호출하기(현재 방식)
        2. `getMember()` 에서 `generateTokens()` 호출하기(고려중인 방식)
        - `MemberService` 에 새로운 메서드를 만들고 해당 메서드에서 `getMember(), generateTokens()` 을 호출하도록 하자!(2번 채택)
- auth
    - [x]  닉네임을 필요로 하는 상황이 회원 기본 정보 조회 API 뿐이어서 `memberContext` 에서 `nickname` 을 빼도 되는가?
        - `memberContext` 에서 `id, authorities` 필드만 두자!

### 다음주 계획(선택)

- member
    - [ ]  회원가입시 `카카오 닉네임 + 난수` 로 기본 닉네임이 설정되도록 변경
    - [ ]  회원 닉네임 수정시 닉네임 중복 체크 추가(+기존 닉네임과 동일한 닉네임으로는 변경 불가)
- member-title
    - [ ]  칭호 부여기준(첫번째 리뷰, 리뷰 5개 이상) 추가하기
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
- GitBook API
    - [ ]  API 명세 작성
