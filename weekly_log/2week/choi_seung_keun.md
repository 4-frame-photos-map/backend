## 최승근

---

### 체크리스트

- [x]  리뷰 조회 관련 리팩터링
- [x]  리뷰 추가 기능 작성
- [x]  리뷰 삭제 기능 작성

### 궁금한점

- 청결도, 보정,  소품 등 평점 관련한 것은 어떤 방식으로 구현할 것인지 의논
    
    → Shop 에 평점이나 총점 등 관련한 필드를 추가해야하는지?
    
- 테스트를 진행하기 위한 카카오 로그인 테스트 방법
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0a48f16d-e289-464d-8218-9afed1f50ef7/Untitled.png)
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/c51b1e42-6157-4a95-9db5-66f0866df5bf/Untitled.png)
    - Redis 사용 관련한 이슈

### 다음주 계획

1. 구현
    - Redis 설치 후 테스트를 통해 리뷰(Review)관련 CRUD 작성 완료 및 리팩터링
    - 회의 내용에 따라 평점(청결도, 보정, 소품) 체크 구현을 -1, 0, 1 방식으로 구현
    - 권한에 따른 접근 방법 설정
