## 최승근


### 체크리스트

- GitBook
    - [x]  응답 메세지 형식 변경에 따른 리뷰 API 응답 메시지 수정
    - [x]  리뷰 응답 DTO 분리에 따른 리뷰 관련 API 명세 수정
- refactoring
    - [ ]  리뷰 API Interceptor 활용한 인가 처리 구현
    - [ ]  응답 데이터 변경에 따른 리뷰 통합 테스트 수정

### 궁금한 점

- 인터셉터 excludePathPatterns 처리 관련
    - /reviews/{review-id}, /reviews/shop/{shop-id} 요청은 AuthenticationInterceptor 제외
    - GET /reviews/{review-id}, POST /reviews/{review-id} 요청 존재
    - 동일한 URL 에서 다른 HTTP Method 처리

### 다음 주 계획

- Review Refactoring
    - [ ]  리뷰 API Interceptor 활용한 인가 처리 구현
    - [ ]  응답 데이터 변경에 따른 리뷰 통합 테스트 수정
