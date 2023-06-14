# 네컷지도 (네컷 잘나오는 포토스팟)
<p align="center"><img src="https://user-images.githubusercontent.com/63441091/235567823-12ace8fa-d695-4cc5-8f3f-6f70a62223f5.jpg" width="300px" height="300px"></p>

<h3 align="center"><a href="https://photosmap.vercel.app/home"> 🔗 네컷지도 바로가기</a></h3>

<br>

## 서비스 기획배경 및 소개

> [MZ 세대 사이에서 즉석사진이 유행](https://maily.so/mediaatelier.official/posts/0cb930d9)하면서 다양한 브랜드가 등장했습니다. 각 브랜드는 콜라보 프레임, 보정도, 해상도, 소품 등의 다양한 서비스를 제공하지만, 이를 비교하기 위해서는 공식 웹사이트, 인스타그램, 지도 서비스의 장소 후기 등 다양한 정보를 수집해야 했습니다. 이에 따라 사용자들이 직접 방문한 즉석사진 브랜드에 대한 청결도, 보정도, 소품 등 각 항목별 리뷰를 작성하고, 검색어나 위치 기반으로 포토부스를 찾아볼 수 있는 서비스를 개발하였습니다. 이를 통해 사용자들은 더욱 쉽게 즉석사진 브랜드를 비교하고 선택할 수 있습니다.

<br>

## 기능 소개

|                소셜 로그인                |            포토부스 지점 찾기             |                 지점 검색                 |
| :---------------------------------------: | :---------------------------------------: | :---------------------------------------: |
| <img src=https://i.imgur.com/3sXKFTj.gif width="250"> | <img src=https://i.imgur.com/VALkI5D.gif width="250"> | <img src=https://i.imgur.com/aZnP7nM.gif width="250"> |
|         <b>내 주변 지점 찾기</b>          |           <b>카카오톡 공유</b>            |             <b>리뷰 작성</b>              |
| <img src=https://i.imgur.com/M3DbgQH.gif width="250"> | <img src=https://i.imgur.com/p0zNspF.gif width="250"> | <img src=https://i.imgur.com/gST2XeA.gif width="250"> |
|          <b>리뷰 수정, 삭제</b>           |             <b>칭호 기능</b>              |              <b>찜 추가</b>               |
| <img src=https://i.imgur.com/rFzQNv9.gif width="250"> | <img src=https://i.imgur.com/Ku48jdD.gif width="250"> | <img src=https://i.imgur.com/Hk0y4BP.gif width="250"> |
|              <b>찜 삭제</b>               |              <b>로그아웃</b>              |              <b>회원탈퇴</b>              |
| <img src=https://i.imgur.com/MLtNhZ8.gif width="250"> | <img src=https://i.imgur.com/LLpoqu9.gif width="250"> | <img src=https://i.imgur.com/qzdzqx7.gif width="250"> |
|              <b>공지사항</b>              |                <b>FAQ</b>                 |
| <img src=https://i.imgur.com/gR0Fyss.gif width="250"> | <img src=https://i.imgur.com/C4DqukY.gif width="250"> ||


## 백엔드 팀원 소개

<table>
    <th width="16.6%" style="text-align:center"><a href="https://github.com/ahah525" target="_blank">한승연</a></th>
    <th width="16.6%" style="text-align:center"><a href="https://github.com/zuminzi" target="_blank">주민지</a></th>
    <th width="16.6%" style="text-align:center"><a href="https://github.com/qkrtpgh5033" target="_blank">박범서</a></th>
    <th width="16.6%" style="text-align:center"><a href="https://github.com/ddackkeun" target="_blank">최승근</a></th>
    <tr>
        <td>
            <img src="https://avatars.githubusercontent.com/u/48237976?v=4" width="160" style="border-radius:100px"/>
        </td>
        <td>
            <img src="https://avatars.githubusercontent.com/u/63441091?v=4" width="160" style="border-radius:100px"/>
        </td>
        <td>
            <img src="https://avatars.githubusercontent.com/u/81248569?v=4" width="160" style="border-radius:100px"/>
        </td>
        <td>
            <img src="https://avatars.githubusercontent.com/u/77659341?v=4" width="160" style="border-radius:100px"/>
        </td>
    </tr>
    <tr>
        <td align="middle">
            <strong>Back-end <br> Developer</strong>
        </td>
        <td align="middle">
            <strong>Back-end <br> Developer</strong>
        </td>
        <td align="middle">
            <strong>Back-end <br> Developer</strong>
        </td>
        <td align="middle">
            <strong>Back-end <br> Developer</strong>
        </td>
    </tr>
</table>

<br>

## 팀 문화
![](https://i.imgur.com/6Mr1NTz.png)


## 백엔드 기술스택

![](https://i.imgur.com/zRInjWk.png)


## ️프로젝트 아키텍처
![](https://i.imgur.com/dSbvq7n.jpg)

## API 명세 및 명세화 도구
<h4><a href="https://organization-ggq.gitbook.io/undefined/">🔗 GitBook 바로가기</a></h4>

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

<br>

## ERD

<img src="https://github.com/4-frame-photos-map/backend/assets/63441091/c11fec1c-2d2b-4a64-8222-53391ed7f9fb" width="900" height="550">

