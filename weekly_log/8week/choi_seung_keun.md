## 최승근


### 체크리스트

- Gitbook
    - [x]  리뷰(Review API)
    - [x]  지점 (Shop API)
        - 민지 님이 작성 (Shop 관련 리팩토링)
    - [ ]  회원 칭호 (MemberTitle API)
        - 작성 중
    
- 배포 관련
    - [ ]  선택 배포 시 테스트 코드 실패 이유 확인
        - 소스코드(application-test.yml)의 정보가 Cloud DB 에 있는지 확인
            - four_cut_photos_map__test 데이터베이스 존재하는지 확인
            - user1@’127.0.0.1’ 계정 정보가 추가되어 있는지 확인
            - user1의 권한 확인
            
            ```
            # 테스트 데이터베이스 생성
            CREATE DATABASE four_cut_photos_map__test
            
            # 데이터베이스 확인
            SHOW DATABASES;
            
            # 계정 생성
            CREATE USER 'user1'@'127.0.0.1' IDENTIFIED BY 'Ttest12341234@';
            
            # 권한 부여
            GRANT ALL PRIVILEGES ON *.* TO 'user1'@'127.0.0.1';
            
            # 권한 적용
            FLUSH PRIVILEGES;
            
            # 권한 부여 확인
            SHOW GRANTS FOR 'user1'@'127.0.0.1';
            ```
            
            ⇒ 해당 문제 아닐 시 NCP 배포 환경과 구조에 대해 찾아봐야 할 것 같습니다.
            
        

### 궁금한 점

- 빌드 파일 배포 시 testDbIp, testDbId, testDbPw 관련 환경 변수를 전달 여부
- 에러 응답(ErrorResponse) 관련 형식 Snake Case 변경
    - 전체적으로 Snake Case 로 변경
    - Snake Case 코드 수정
        
        ```
        // 기존 코드
        @JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
        
        // 변경 코드
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        ```
        
    
- 전체 회의 내용 바탕으로 추가적인 개발 사항 & 수정 사항
    - 리스트 대표 이미지
    - 지점(shop) 상세 페이지 내부 정보
        - 후기의 결제 방식, 가격
    

### 참고 자료

- Gitbook API 작성 방법
    - [https://baeminchan-project.gitbook.io/project/undefined-5](https://baeminchan-project.gitbook.io/project/undefined-5)
- 배포 시 테스트 코드 오류 관해 찾아본 자료
    - [https://www.inflearn.com/questions/327939/gradle-테스트와-빌드-질문](https://www.inflearn.com/questions/327939/gradle-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EB%B9%8C%EB%93%9C-%EC%A7%88%EB%AC%B8)
    - [https://velog.io/@doli0913/배포-과정에서-테스트-코드의-불편사항-개선](https://velog.io/@doli0913/%EB%B0%B0%ED%8F%AC-%EA%B3%BC%EC%A0%95%EC%97%90%EC%84%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C%EC%9D%98-%EB%B6%88%ED%8E%B8%EC%82%AC%ED%95%AD-%EA%B0%9C%EC%84%A0)

### 다음 주 계획

- gitbook 이용한 API 문서화 완료하기
    - favorites
    - shop-title-logs
    - member-title
- 배포 시 테스트 문제 관련 NCP 배포 환경과 구조 찾아보기
- 브랜드 정보를 담고 있는 brand Entity 설계(shop 연관 관계 설정)
- Shop 과 연관된 brand 는 기본 값 기타로 설정, 브랜드 정보가 있는 경우 해당 brand 교체
- 화면 설계 중 이미지가 필요한 API 응답에 brand DTO 값을 함께 응답하도록 수정
    - 키워드 상점 조회 (/shops)
    - 모든 브랜드 상점 조회 (/shops/marker)
    - 대표 브랜드 상점 조회 (/shops/brand)
    - 찜한 상점 조회 (/favorites)
    - 상점 상세 조회 (/shops/{shop-id})
