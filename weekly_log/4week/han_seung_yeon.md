## 한승연

### 체크리스트(필수)

- member
    - [x]  Member → MemberTitleLog 단방향 매핑으로 수정
        - [x]  회원가입 칭호 부여 로직 수정
        - [x]  회원칭호 전체 조회 로직 수정
        - [x]  회원기본정보 조회 로직 수정
    - [x]  닉네임 수정 API 개발 및 문서 작성
    - [x]  회원탈퇴 URI DELETE /member 로 변경
    - [x]  대표 칭호 수정 API 개발 및 문서 작성
- auth
    - [x]  토큰 재발급 URI POST /auth/token 으로 변경 및 application-base-addi.yml 수정
- favorite
    - [ ]  찜 추가시 조건에 따라 회원 칭호 부여하는 로직 추가

### 참고자료(선택)

- #1 [[JPA] FK 로 조회할 때 findByXXXId() VS findByXXX()](https://www.notion.so/JPA-FK-findByXXXId-VS-findByXXX-81cdb3b44e8a4e7bbc7dad6dcbb543ae)
- #2 [[MySQL 오류 해결] SQLIntegrityConstraintViolationException](https://www.notion.so/MySQL-SQLIntegrityConstraintViolationException-4bae6fd8366540e29f1a7a2b3dfd5ea9)
- application-base-addi.yml 변경

### 궁금한점(선택)

- [ ]  해당 글(#1)의 결론에서 말한 것처럼 findByXXX() 으로 조회하면 FK 객체가 필요하다. 현재는 `memberContext.getMember()` 를 통해 Member 객체를 얻고 있다. 이 방법처럼 실제 DB 에서 조회한 Member 객체가 아닌 PK(memberId)값이 있는 Member 객체를 생성해서 사용하는 방식도 문제가 없는가??
    
    ```java
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/main-title/{member-title-id}")
    public ResponseEntity<RsData> updateMainMemberTitle(
            @AuthenticationPrincipal MemberContext memberContext,
            @PathVariable(value = "member-title-id") Long memberTitleId
    ) {
        //TODO: 2가지 방식 고민중
    // 1. 기존처럼 member 객체를 넘기는 방법
        // 2. memberId 만 넘기고 실질적으로 조회쿼리가 날라가는 memberTitleService.updateMainMemberTitle() 내에서 member 객체를 만들어 사용하는 방식
        memberService.updateMainMemberTitle(memberContext.getMember(), memberTitleId);
        RsData<?> body = new RsData<>(
                true, "회원 대표 칭호 수정 성공", null
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
    ```
    
- [ ]  해당 글(#2)에서 제시한 2가지 해결방법 중 어느 것으로 구현하는 것이 더 나은가?
    - 일단은 단방향 매핑 방식으로 가기로 결정
- [ ]  아래 2가지 방식에서 생길 수 있는 문제를 감수하고서라도 일단은 구현하는게 맞는가? 아니면 회원 칭호 부여 기능 구현에 관해 Spring Batch + Scheduler 도입을 해야하나??
    1. `favoriteService.save()` 메서드 내부에서 조건을 검사하고 회원 칭호를 부여하면 찜 추가는 성공하고, 회원칭호 부여에서 실패했을 때 찜추가까지 전체 롤백된다… 회원 칭호와 별개로 찜추가 자체는 되야 하는게 맞는거 아닌가?
    2. `favoriteService.save()` 메서드 외부에서 조건을 검사하고 회원 칭호를 부여하게 되면 회원칭호 부여가 실패했을 경우에 대한 대처법이 없다.. 
    
    → Spring Batch + Scheduler 도입 확정
    

### 다음주 계획(선택)

- [ ]  PR 리뷰 리팩토링
- member
    - [ ]  인증과정을 거칠때마다 MemberContext 객체를 만들어 SpringSecurity 에 등록해주기 위해 DB 에서 Member 를 조회하는 문제 해결하기
    - [ ]  테스트 코드 작성
- favorite
    - [ ]  찜 추가시 조건에 따라 회원 칭호 부여하는 로직 추가
- review
    - [ ]  리뷰 작성시 조건에 따라 칭호 부여하는 로직 추가
- [ ]  jpa cascade.remove vs orphanRemoval 공부
- [ ]  spring batch 공부
