# 일지

## 이름 박범서

### 체크리스트(필수)

- [x]  Cloud DB Shop Data 반영
- [x]  도메인 구매
    - *.photosmap.site
- [x]  ssl
- [x]  CI/CD
    - Github Action - Docker

### 에러 사항

- 서버가 계속해서 정지됨
- 수시로 접속하니 네이버 클라우드 플랫폼 고객센터에서 연락
- 내용 : 외부 다수 스캔으로 인해 조치를 취한다던가 ( 포트 ), 서버를 반납 후 다시 생성해주셔야 한다.

### 의논 사항

- yml DB
    - MySQL
    

### 공유 사항

- application-base-addi.yml
    - Github Actions으로 빌드해서 도커 허브에 올리는 과정에 있어서 applications-base-addi.yml 파일을 어떻게 포함 시키는가에 대한 고민이 생김
    - 해결 방법)
        1. JasyptConfig을 통해서 applications-base-addi.yml 파일을 없애고 민감한 부분들을 전부 인코딩해서 사용
        2. github Action - workflows 활용
- 배포 테스트 오류
    
    ```bash
    sudo ./gradlew clean build
    
    ./gradlew bootJar
    ```
    

### 다음주 계획

- yml 파일 수정
- CI/CD 테스트
    - 테스트 코드 주석을 빼고
- ACG
    - DB, REDIS : 서버
    - SSH  : 본인
- 인덱스 처리
    - 플레이스 네임
    - 도로명 주소
- 스토리지 서버
