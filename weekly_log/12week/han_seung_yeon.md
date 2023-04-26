## 한승연

### 체크리스트(필수)

- aws
    - [x]  CloudFront, S3 연동
- file
    - [x]  #1 `feat` : CloudFront 도메인명 yml 추가, 이미지 업로드 시 CloudFront 도메인으로 이미지 URL 응답
    - [x]  `feat` : 단일 이미지 업로드 API 개발
    - [ ]  `feat` : 이미지 리사이징
    - [ ]  `feat` : 파일 DB 설계
- auth
    - [x]  `feat` : 액세스 토큰과 리프레쉬 토큰 만료 에러 응답 구분(리프레쉬 토큰 검증은 try/catch 로 직접 예외처리), GitBook 반영
    - [ ]  `refactor` : RestTemplate 대신 WebClient 사용
- brand
    - [x]  `refactor` : DB 브랜드 이미지 CloudFront URL 로 변경

### 참고자료(선택)

- #1 [[Spring Boot] CloudFront 적용하기](https://www.notion.so/Spring-Boot-CloudFront-101a3f062a834699a8577b5cd9f509f4)

### 다음주 계획
- [ ] 즉석사진관 지점명 크롤링 자료조사
