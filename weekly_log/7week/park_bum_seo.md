
## 박범서

### 체크리스트(필수)

- [x]  Cloud DB for MySQL ( 네이버 클라우드 플랫폼 )
    - yml 수정
- [x]  배포 포트 변경 : 8090 → 8080
- [x]  nignx 프록시 : 80 → 8080
- [ ]  도메인
    - 4frame-map
    - photos-map
- [ ]  ssl
- [ ]  CI/CD

### 에러 사항

**대소문자 구별로 인해, 테이블 인식을 못하는 오류**

![스크린샷 2023-03-15 오후 6 01 15](https://user-images.githubusercontent.com/81248569/225333008-6cf20b44-91be-44df-b0d3-5f5d63025f82.png)

**원인 → 기본값이 0으로 되어있음**

![스크린샷 2023-03-15 오후 6 01 27](https://user-images.githubusercontent.com/81248569/225333127-2499785d-b462-47e4-9014-537a6e086eed.png)

**해결**

![스크린샷 2023-03-15 오후 6 01 51](https://user-images.githubusercontent.com/81248569/225333180-1230ae4b-f712-4185-bcc7-21595b9c7f1e.png)
### 의논 사항

- 도메인 구매
    - four-frame-map (1,900)
    - **photosmap (1,900)**
- DB 계정
    - 유저명, 비번
        
        
- yml 관리
    - JasyptConfig → 무중단 배포할 때 쓸 수 있지 않을까 생각

### 공유 사항

- 데이터 베이스 스키마
    - four_cut_photos_map (`배포`)
    - ~~four_cut_photos_map_dev (`개발`)~~
    - four_cut_photos_map_test (`테스트`)

### 다음주 계획(선택)

- 도메인
    - 4frame-map
    - photos-map
- ssl
- CI/CD
