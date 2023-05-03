## 최승근


### 체크리스트

- 리뷰 테스트(`test`)
    - `feat` : 리뷰 단위 테스트
        - ReviewService 단위 테스트
            - [x]  리뷰 단건 조회(성공, 실패 테스트)
            - [x]  특정 리뷰 수정(성공, 실패 테스트)
            - [x]  특정 리뷰 삭제(실패 테스트)
            - [x]  회원 전체 리뷰 조회(성공, 실패 테스트)
            - [x]  지점 전체 리뷰 조회(성공, 실패 테스트)
            - [ ]  상점 리뷰 작성(성공, 실패 테스트)
    - `refactor` : 리뷰 통합 테스트
        - [ ]  리뷰 로직, 응답 데이터 변경에 따른 테스트 결과 수정

### 궁금한 점

- `지점 리뷰 작성`,  `특정 리뷰 수정`, `특정 리뷰 삭제` 내부에서 상점 리뷰 정보를 갱신하는 `updateShopReviewStats` 사용하는데 해당 부분을 Controller 단으로 옮기는 것이 어떠한지?
    - ShopService 에서 실행해야 하는 로직이 아닌가 하는 생각이 들었습니다.
    - Shop 갱신 작업에서 트랜잭션이 문제가 될지?

### 참고 자료

- mockito 참고 사이트
    - [https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#2](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#2)
    - [https://covenant.tistory.com/256](https://covenant.tistory.com/256)
    - [https://velog.io/@hellonayeon/spring-boot-service-layer-unit-testcode](https://velog.io/@hellonayeon/spring-boot-service-layer-unit-testcode)

### 다음 주 계획

- 리뷰 통합 테스트 수정 마무리
- 지점 리뷰 작성, 특정 리뷰 수정, 특정 리뷰 삭제 시 상점 리뷰 정보 갱신하는 로직 분리
- 팀원 분들과 의논하여 추가적으로 진행해야 할 것 있으면 진행
