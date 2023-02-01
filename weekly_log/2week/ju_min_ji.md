## 주민지

### 체크리스트(필수)

- [x]  찜 테이블 설계
  - [x] 연관관계 재매핑
  - [x] `Cascade` 설정
- [x] 로그인 회원 본인의 찜 리스트 조회 기능
  - [x] MemberContext에 memberContext(현재 로그인 유저 정보)와 member(조회하려는 유저 정보) 비교 메서드 추가
    - `memberIsNot()`, `MemberIs()`
  - [x] FavoriteResponseDto 추가 및 EntityToDto 변환 로직 추가
  - [x] memberContex와 member 불일치 에러 코드(`MEMBER_MISMATCH`) 추가 
- [ ] 찜 추가 기능
  - [x] MemberContext에서 member 바로 리턴하는 메서드 추가
      - `getMember()`
  - [ ] +1 된 해당 상점의 찜 개수 반환 
### 참고자료(선택)

- REST API 설계 가이드
  - https://sharplee7.tistory.com/49
  - https://sanghaklee.tistory.com/57
- 테이블 명명 규칙 : 찜 테이블을 `Favorites(noun, 즐겨찾기, 복수)`가 아닌 `Favorite(noun, 특히 좋아하는 것, 단수)`으로 설정한 이유
  - [테이블 명명규칙 7가지](https://killu.tistory.com/52)
  - [테이블명을 단수로 설정해야 하는 이유](https://edunga1.gitbooks.io/catlogic/content/database/table-naming-convention.html)
  - [JPA-Cascade(영속성전이)](https://hongchangsub.com/jpa-cascade-2/)
  - [HTTP Status Code](https://developer.mozilla.org/ko/docs/Web/HTTP/Status/403)
### 궁금한점(선택)

- Q1.사용자 인증 `Filter`에서 `인증되지 않은 사용자(찾을 수 없는 사용자)(토큰 만료 사용자)인지` 검사하는데도 `Controller`에서도 memberContext null 체크를 해야하는지? 
- A1. `@PreAuthorize`로 권한 체크 + memberContext null 체크 같이 진행하기
### 다음주 계획(선택)

- FavoriteDto에서 사용하는 외래키 `member`, `shop` DTO 추가 (순환참조 방지)
- 찜 추가 기능 
- 찜 취소 기능
- keywordSearch Controller code 서비스 단위로 모듈화
- RsData -> ResponseEntity<RsData>로 응답구조 통일
