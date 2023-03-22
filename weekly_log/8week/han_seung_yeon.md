## 한승연

### 체크리스트(필수)

- global
    - [x]  `refactor` : RedisDao 에 redis key 생성 메서드 정의해서 사용, 테스트에도 적용
    - [x]  `test` : DatabaseCleaner 테스트 실패 해결
- member
    - [x]  `refactor` : 회원가입 및 닉네임 수정시 redis 에 nickname 저장안함
    - [x]  `refactor` : 한 트랜잭션 내에서 회원조회(새로운 회원은 회원가입), 토큰 발급이 수행되도록 변경
    - [x]  닉네임 중복 방지 설정
        - [x]  `feat` : 회원가입시 `카카오 닉네임 + 4자리 난수` 로 유니크한 닉네임을 생성하여 저장
        - [x]  `feat` : 회원 닉네임 수정시 닉네임 중복 체크 추가
        - [x]  `test` : 회원가입, 닉네임 수정 테스트 코드 수정
    - [x]  `fix` : 로그인 상태에서만 로그아웃 가능하도록 수정, 로그아웃 후 redis 에서 refreshToken 삭제되지 않는 오류 해결
    - [x]  `docs` : GitBook API 명세 작성
- auth
    - [x]  `refactor` : MemberContext 의 id, authorities 필드 외 모든 필드 삭제, MemberContext 객체 생성시 redis 에서 nickname 조회하는 로직 삭제
    - [x]  `docs` : GitBook API 명세 작성
- member-title
    - [x]  `feat` : 칭호 부여기준(첫번째 리뷰, 리뷰 5개 이상) 추가하기
    - [x]  #1 `fix` : 칭호 부여, 칭호 부여 기준 부합 여부 검사 메서드는 `MemberService` → `CollectService` 로 분리

### 참고자료(선택)

- #1 [[Spring Boot 오류 해결] 순환 참조 문제(The dependencies of some of the beans in the application context form a cycle:)](https://www.notion.so/Spring-Boot-The-dependencies-of-some-of-the-beans-in-the-application-context-form-a--95cc9ab39dcf432e8cfba26f3923dfe5)

### 궁금한점(선택)

- [x]  회원가입 뉴비 칭호 부여 시점을 언제로 할지
    1. 회원가입 직후(다른 트랜잭션으로) → 채택!
        - 그럴일은 거의 없겠지만 회원가입 이후 뉴비 칭호 부여하는 곳에서 에러가 날 경우에는 뉴비 칭호를 못받게 되는 상황이 생길 수 있음
            
            → 기존 스케줄링에 뉴비 칭호를 그대로 포함시키면 문제 해결가능
            
    2. 회원가입 후 자정(스케줄링으로)
    - 보통은 뱃지를 받았을 때 알림이나 모달로 획득사실을 유저에게 알려주는데, 우리는 직접 칭호에 들어가야만 그 사실을 인지할 수 있음
    - 유저의 흥미를 유발이 목적이라면 직접 칭호에 들어가지 않고도 새로운 칭호를 부여받았다는 사실을 알려주어야 하지 않을까?

### 다음주 계획(선택)

- 공통
    - [ ]  `test` : 테스트 코드에 `assertAll()` 적용
- member
    - [ ]  `test` : MemberService 테스트 코드 작성
    - [ ]  `fix` : 회원 탈퇴시 Member 삭제하기 전 Member 를 참조하고 있는 엔티티(Review) 삭제하기
- auth
    - [ ]  `feat` : 회원가입시 뉴비 칭호 즉시 부여하도록 변경
    - [ ]  `test` : JwtService 테스트 코드 작성
    - [ ]  `refactor` : accessToken 블랙리스트 관리하지 않는 방식으로 변경
- member-title
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
