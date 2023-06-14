## 한승연

### 체크리스트(필수)

- global
    - [x]  #3 단위 테스트 성공, 통합 테스트 실패하는 문제 → 테스트 격리로 해결
- auth
    - [x]  로그아웃/탈퇴한 Member 의 JWT AccessToken 블랙리스트 관리
        - key = jwt_black_list:{jwtAccessToken}
        - value = {withdrawl/logout}
    - [x]  #1 인증과정을 거칠때마다 MemberContext 객체를 만들어 SpringSecurity 에 등록해주기 위해 DB 에서 Member 를 조회하는 문제 해결하기
- member
    - [x]  회원가입 로직 수정
        - [x]  회원가입시 칭호 부여하는 코드 서비스 메서드 내에서 삭제
            - 회원가입 후 바로 칭호가 부여되지 않기 때문에 회원가입 당일에는 대표 칭호가 없을 수 있다.
        - [x]  회원가입시 redis 에 닉네임 저장(`member:{memberId}:nickname` , nickname)
    - [x]  #2 대표 칭호가 없는 경우 응답값이 넘어오지 않는 문제 해결(회원 기본 정보 조회 로직 수정)
        - [x]  대표 칭호가 없는 경우도 정상적인 상황이므로 `MemberInfoResp` 를 리턴하도로 수정
        - [x]  `MemberInfoResp` 에 `@JsonInclude(JsonInclude.Include.*NON_NULL*)` 을 추가하여 null 인 값은 응답에서 제외되도록 설정
    - [x]  회원 닉네임 수정 로직 변경(redis 에 저장된 nickname 값을 수정하는 코드 추가)
- member-title
    - [x]  회원 칭호를 부여할 수 있는지 검사, 부여하는 로직 수정
        - [x]  회원가입 칭호와 다른 칭호를 같은 날에 부여 받는 경우 회원가입 칭호를 대표 칭호로 설정
            - e.g. 1일: 회원가입, 찜 5개 이상 → 2일 자정: 뉴비(대표 칭호), 찜첫걸음, 찜홀릭 칭호 부여
        - [x]  칭호 부여기준(회원가입) 추가하기
    - [x]  뉴비, 찜 첫 걸음, 찜 홀릭 테스트 코드 작성

### 참고자료(선택)

- #1 [Spring Security 에 Authentication 객체를 등록하기 위해 DB 에서 Member 를 조회하는 문제 해결하기](https://www.notion.so/Spring-Security-Authentication-DB-Member-214cc208d08c4198b9edf0f4bc89a4b0)
- #2 대표 칭호가 없는 경우 응답값이 넘어오지 않는 문제 해결
    1. 기존 로직
        - 대표 칭호가 있는 경우만 `MemberInfoResp` 을 리턴한다.
        - 대표 칭호가 없는 경우에는 `null` 을 리턴한다.
        
        ```java
        public MemberInfoResp getMemberInfo(Long id) {
            Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
            log.info("----Before memberTitleService.findByMember(member)----");
            List<MemberTitleLog> memberTitleLogs = memberTitleService.findByMember(member);
            // 대표 칭호 조회
            for(MemberTitleLog memberTitleLog : memberTitleLogs) {
                if(memberTitleLog.getIsMain()) {
                    log.info("----Before memberTitleLog.getMemberTitleName()----");
                    return MemberInfoResp.toDto(member, memberTitleLog.getMemberTitleName(), memberTitleLogs.size());
                }
            }
            return null;
        }
        ```
        
        ```json
        {
            "success": true,
            "message": "회원 정보 조회 성공"
        }
        ```
        
    2. 수정 로직
        - 대표 칭호 유무와 상관없이 `MemberInfoResp` 을 리턴한다.
        - 대표 칭호가 있을 때만 `mainMemberTitle` 값이 응답에 포함된다.
        
        ```java
        // 회원 id 로 기본 정보 조회
        public MemberInfoResp getMemberInfo(Long id) {
            Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        log.info("----Before memberTitleService.findByMember(member)----");
            List<MemberTitleLog> memberTitleLogs = memberTitleService.findByMember(member);
            // 대표 칭호 조회(회원가입 후 바로 칭호가 부여되지 않기 때문에 회원가입 당일에는 대표 칭호가 없을 수 있음)
            String mainMemberTitle = null;
            for(MemberTitleLog memberTitleLog : memberTitleLogs) {
                if(memberTitleLog.getIsMain()) {
        log.info("----Before memberTitleLog.getMemberTitleName()----");
                    mainMemberTitle = memberTitleLog.getMemberTitleName();
                    break;
                }
            }
            return MemberInfoResp.toDto(member, mainMemberTitle, memberTitleLogs.size());
        }
        ```
        
        ```json
        {
            "success": true,
            "message": "회원 정보 조회 성공",
            "result": {
                "id": 101,
                "nickname": "승연",
                "memberTitleCnt": 0
            }
        }
        ```
        
- #3 [[Spring Boot 오류 해결] 단위 테스트 성공, 통합 테스트 실패 문제 테스트 격리로 해결하기](https://www.notion.so/dfd5ef33b30f42aea19fe4c3312ad2d2)

### 궁금한점(선택)

- auth
    - [x]  MemberContext 에서 id, nickname, authorities 를 제외한 나머지 필드를 삭제해도 되는지? (현재 다른 곳에서 쓰이지 않음)
- shop
    - [x]  merge 할 때 Shop 생성자 때문에 오류가 나서 임시로 추가해두었음

### 다음주 계획(선택)

---

- global
    - [ ]  DatabaseCleaner 테스트 코드 작성
- member-title
    - [ ]  칭호 부여기준(첫번째 리뷰, 리뷰 5개 이상) 추가하기
    - [ ]  리뷰 첫 걸음, 리뷰 홀릭 칭호 부여 테스트 코드 작성
    - [ ]  칭호 부여하기 위한 조회 대상을 전체 회원에서 일부 회원으로 줄일 수 있을지 방법 찾아보기
        - e.g. 오늘 하루 내에 로그인 요청을 한 회원 / 인증이 필요한 API 요청을 한 회원
