# 네컷지도

##  서비스 소개
<h4> <a href="https://photosmap.vercel.app/home">🗺 네컷지도 바로가기</a> </h4>

셀프 즉석사진 찍을 장소를 비교하고 싶다면, 네컷 사진 지도를 이용해보세요!

가까운 지점 정보와 청결도, 보정도, 소품 수 등 다양한 정보를 확인할 수 있어요.

##  API 문서
<h4><a href="https://four-cut-photos-map.gitbook.io/four-cut-photos-map/">📄 GitBook 바로가기</a></h4>

##  사용 기술
|  | Stack |
|--|--|
|Language | <img src="https://img.shields.io/badge/java 17-007396?style=for-the-badge&logo=java&logoColor=white">|
| BackEnd | <img src="https://img.shields.io/badge/springboot 2.7.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/JPA-273347?style=for-the-badge&logoColor=white"> <img src="https://img.shields.io/badge/kakao API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"> <img src="https://img.shields.io/badge/slf4j-03C75A?style=for-the-badge&logo=slf4j&logoColor=white">|
|Build Tool|<img src="https://img.shields.io/badge/gradle 7.5-02303A?style=for-the-badge&logo=gradle&logoColor=white">|
|Database|<img src="https://img.shields.io/badge/mysql 2.7.7-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">|
|협업|<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/GitBook-3884FF?style=for-the-badge&logo=GitBook&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">|
|IDE|<img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white">|
|배포|<img src="https://img.shields.io/badge/NCP-03C75A?style=for-the-badge&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">|


##  주요 기능
- 소셜 로그인
    - 카카오 로그인으로 회원가입 및 로그인
- 지점 찾기
    - 현재 위치를 중심으로 주변 지점 찾기(Kakao Map 마커, 리스트로 제공)
        - 브랜드(전체, 인생네컷, 하루필름, 포토이즘, 포토그레이)별 필터 기능 제공
    - 키워드 검색으로 지점 찾기(Kakao Map 마커, 리스트로 제공)
- 리뷰
    - 지점 리뷰(별점, 글, 청결도/보정/소품 평가) 작성, 조회, 수정, 삭제
- 찜
    - 지점 찜 추가, 삭제, 조회
- 회원 칭호 부여
    - 부여 기준(회원가입/리뷰 개수/찜 개수)을 만족한 회원에게 회원 칭호를 부여
- 지점 칭호 부여
    - 매달 부여 기준(찜 수/청결도 점수)을 만족한 지점에 지점 칭호를 부여

## ERD
<img src ="https://user-images.githubusercontent.com/48237976/234591443-813c51ed-c510-4fa2-8754-f2dd432fe7f2.png" width="700" height="550">
