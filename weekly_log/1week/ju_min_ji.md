## 주민지

### 체크리스트(필수)

- [X] SQL Shop Table 샘플데이터 실제 맵 데이터로 수정
- [X]  키워드 검색 리스트 로직 수정
  - [X] KaKaoAPI 응답리턴타입 수정 ( `ResponseEntity<DTO>` -> `DTO` )
    - 수정 전
    <img width="650" src="https://user-images.githubusercontent.com/63441091/214583457-4107432f-7ed7-4abf-9f83-9e5d433bac34.png">

    - 수정 후
    <img width="650" alt="스크린샷 2023-01-24 오후 4 14 04" src="https://user-images.githubusercontent.com/63441091/214582050-8b72dbd1-d4f5-4f92-b4ed-56743c5810b8.png">

  - [X] 카카오 api 응답 `DTO` 에서 `List<DTO.Document>`로 변환 로직 추가
  - [X] DB와 비교 로직 수정
    - `지역명 + 포토부스 브랜드명`이나 `포토부스 브랜드명 + 지점명`으로 검색 가능
    - 프론트에서 사용자에게 위와 같은 검색 가이드라인 제시 필요
  - [X] 응답데이터 ResponseBody 구조 통일 (code, message, result)
### 참고자료(선택)
- [ Kakao API 공식문서 - 키워드로 검색하기](https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword )
- [ DTO 타입의 List for문 사용 시 주의할 점 ](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=dududtnr&logNo=221487314964)

### 다음주 계획(선택)
  - [ ] 찜 기능 구현
  - [ ] 찜 목록 페이지 구현
