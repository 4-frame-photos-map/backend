## 최승근

---

### 체크리스트

- 리뷰 통합 테스트
    - 특정 리뷰 테스트
        - [x]  특정 리뷰 조회 테스트
        - [x]  특정 리뷰 수정 테스트
        - [x]  특정 리뷰 삭제 테스트
    - 상점 리뷰 테스트
        - [x]  상점의 전체 리뷰 조회 테스트
        - [x]  상점의 리뷰 추가 테스트

### 참고 자료

- 통합 테스트의  종류와 각 테스트 사용 방법 참고
    - [https://wooody92.github.io/spring boot/Spring-Boot-통합테스트와-단위테스트/](https://wooody92.github.io/spring%20boot/Spring-Boot-%ED%86%B5%ED%95%A9%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8/)
    - 적용한 방법 : 서블릿을 mocking 하여 사용하는 MockMvc 방법
- 통합 테스트에서 테스트 격리 방법
    - [https://tecoble.techcourse.co.kr/post/2020-09-15-test-isolation/](https://tecoble.techcourse.co.kr/post/2020-09-15-test-isolation/)
    - 데이터의 초기 상태 유지를 위한 테스트 격리 필요
    - 적용한 방법 : EntityManager로 직접 TRUNCATE 쿼리 실행
        - databaseCleanUp 클래스 생성 방법 ⇒ 팀원 분들도 동일한 방법으로 해결
        

### 궁금한 점

- 칭호를 부여하는 과정에서 배치를 사용 여부 질문
    
    ⇒ 논의 결과 : 예시가 없어서 현재 고민하는 단계 (추후 고민)
    

### 다음 주 계획

- FrontEnd 개발자분들과 API 공유, 개발을 위한 GitBook 사용 방법 찾기
- 배포 시 테스트 코드 실패 이유 확인 및 코드 수정
