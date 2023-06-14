
## 최승근


### 체크리스트

- Review
    - [x]  상점 리뷰, 회원 리뷰 관련 API 수정
        - /reviews/shop/{shop-id}
        - /reviews/member
    - [x]  Review 테스트 코드 수정
        - 요청 API 수정 관련, REVIEW_NOT_FOUND 관련
    - [x]  리뷰 서비스 내부 로직 리팩터링
        - 리뷰 없을 때 정상 응답하도록 수정 ⇒ 특정 리뷰 검색의 경우 REVIEW_NOT_FOUND
        - 회원(Member) 검증 코드 삭제
        - @Transactional 관련 부분 수정
- Gitbook
    - [x]  관련 자료 조회 및 작성해보기
        
        ⇒ 일단 몇 가지 작성만 해봄
        

### 궁금한 점

- Review의 청결, 보정, 소품에 관하여 integer (-1, 0, 1)와 String 중 어떤 것으로 사용하는 것이 좋을지
    
    ⇒ 현재 사용 방식 유지, 추후 프론트 분들과 의논 후 변경 시 리팩토링 예정
    
- Gitbook 관련하여 작성 형식
    
    ⇒ 자료형을 출력하는 방식이 아닌 예상 응답 값을 적는 것으로 설정
    

### 참고 자료

- Spring boot 와 Gitbook 연동
    - [https://docs.gitbook.com/content-creation/blocks/openap](https://docs.gitbook.com/content-creation/blocks/openapi)
        
        ⇒ 스프링 자체에서 연동은 불가능하며 Swagger 파일이나 URL 연동가능
        
    
- Gitbook API 작성 방법
    - [https://nate-crema.gitbook.io/aj-mealmap-api/internal/v1.0/users](https://nate-crema.gitbook.io/aj-mealmap-api/internal/v1.0/users)


### 다음 주 계획

- 배포 시 테스트 코드 실패 이유 확인 및 코드 수정
- Review API , SHOP API 관련 Gitbook 작성하기
