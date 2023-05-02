# 네컷지도 (네컷 잘나오는 포토스팟)
<p align="center"><img src="https://user-images.githubusercontent.com/63441091/235567823-12ace8fa-d695-4cc5-8f3f-6f70a62223f5.jpg" width="300px" height="300px"></p>

<h3 align="center"><a href="https://photosmap.vercel.app/home">✔️ 네컷지도 바로가기</a></h3>


## 💡 서비스 기획배경 및 소개

> [MZ 세대 사이에서 즉석사진이 유행](https://maily.so/mediaatelier.official/posts/0cb930d9)하면서 다양한 브랜드가 등장했습니다. 각 브랜드는 콜라보 프레임, 보정도, 해상도, 소품 등의 다양한 서비스를 제공하지만, 이를 비교하기 위해서는 공식 웹사이트, 인스타그램, 지도 서비스의 장소 후기 등 다양한 정보를 수집해야 했습니다. 이에 따라 사용자들이 직접 방문한 즉석사진 브랜드에 대한 청결도, 보정도, 소품 등 각 항목별 리뷰를 작성하고, 검색어나 위치 기반으로 포토부스를 찾아볼 수 있는 서비스를 개발하였습니다. 이를 통해 사용자들은 더욱 쉽게 즉석사진 브랜드를 비교하고 선택할 수 있습니다.

- 가입 절차 최소화를 위해 카카오 로그인으로 회원가입 및 로그인을 진행합니다.
- 내 주변 반경 5km 이내 포토부스 지점을 브랜드 별로 확인할 수 있습니다.
- 검색어나 브랜드 필터링을 통해 원하는 지점을 조회할 수 있습니다. 
- 각 항목(별점, 코멘트, 청결도/보정/소품 만족도 평가) 별로 리뷰 작성 및 조회 가능합니다.
- 마음에 드는 지점을 찜하여 찜 목록에서 모아볼 수 있습니다.
- 특정 기준(회원가입/리뷰 개수/찜 개수)을 만족한 회원에게 회원 칭호가 부여됩니다.
- 특정 기준(찜 수/청결도 점수)을 만족한 지점에는 지점 칭호가 부여됩니다.

## 🧑‍💻 백엔드 팀원 소개
|박범서|주민지|최승근|한승연|
|----|-----|----|----|
|[qkrtpgh5033](https://github.com/qkrtpgh5033)|[zuminzi](https://github.com/zuminzi)|[ddackkeun](https://github.com/ddackkeun)|[ahah525](https://github.com/ahah525)|

## 🛠 사용 기술
|  | Stack |
|--|--|
|Language | <img src="https://img.shields.io/badge/java 17-007396?style=for-the-badge&logo=java&logoColor=white">|
| BackEnd | <img src="https://img.shields.io/badge/springboot 2.7.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/JPA-273347?style=for-the-badge&logoColor=white"> <img src="https://img.shields.io/badge/kakao API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"> <img src="https://img.shields.io/badge/slf4j-03C75A?style=for-the-badge&logo=slf4j&logoColor=white"> <img src="https://img.shields.io/badge/jsoup-00AFF0?style=for-the-badge&logoColor=white">|
|Build Tool|<img src="https://img.shields.io/badge/gradle 7.5-02303A?style=for-the-badge&logo=gradle&logoColor=white">|
|Database|<img src="https://img.shields.io/badge/mysql 8.0.25-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">|
|협업|<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/GitBook-3884FF?style=for-the-badge&logo=GitBook&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">|
|배포|<img src="https://img.shields.io/badge/NCP-03C75A?style=for-the-badge&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">|

## 📄 API 명세 및 명세화 도구
<h4><a href="https://four-cut-photos-map.gitbook.io/four-cut-photos-map/">✔️ GitBook 바로가기</a></h4>

> GitBook을 이용하여 API 명세를 관리했습니다.


|API| URI   |
|----|------|
|카카오 로그인 리다이렉트|`GET /auth/login/kakao`|
|서비스 로그아웃|`GET /auth/logout`|
|access token 재발급|`POST /auth/token`|
|회원 프로필 조회|`GET /member/info`|
|회원 닉네임 중복체크|`GET /member/nickname`|
|회원 닉네임 수정|`PATCH /member/nickname`|
|회원 탈퇴|`DELETE /member`|
|회원 대표 칭호 수정|`PATCH /member/main-title/{member-title-id}`|
|키워드로 지점 조회|`GET /shops`|
|반경 내 브랜드 별 지점 조회|`GET /shops/brand`|
|지점 상세 조회|`GET /shops/{shop-id}`|
|Map Marker 모달용 지점 간단 조회|`GET /shops/{shop-id}/info`|
|리뷰 단건 조회|`GET /reviews/{review-id}`|
|지점 전체 리뷰 조회|`GET /reviews/shop/{shop-id}`|
|회원 전체 리뷰 조회|`GET /reviews/member`|
|지점 리뷰 작성|`POST /reviews/shop/{shop-id}`|
|특정 리뷰 수정|`PATCH /reviews/{review-id}`|
|특정 리뷰 삭제|`DELETE /reviews/{review-id}`|
|찜한 지점 전체 조회|`GET /favorites`|
|찜 추가|`POST /favorites/{shop-id}`|
|찜 취소|`DELETE /favorites/{shop-id}`|
|회원 칭호 단건 조회|`GET /member-titles/{id}`|
|회원 칭호 전체 조회|`GET /member-titles`|

## 💾 ERD
<img src ="https://user-images.githubusercontent.com/63441091/235579800-862dd134-c75a-44f0-83d5-1cd90e9125fe.png" width="700" height="550">
