## 한승연

### 체크리스트(필수)

- auth
    - [x]  `fix` : Redis에 저장된 refresh token 이 만료 전에 삭제되는 문제 해결
    - [x]  `refactor` : 잘못된 refresh token 예외처리 응답 추가, GitBook 반영
- crawl
    - [x]  `feat` : 대표브랜드(인생네컷, 하루필름, 포토이즘, 포토그레이) 지점명, 주소 크롤링 및 데이터 전처리
    - [x]  `feat` : 포토시그니처 지점명, 주소 크롤링 및 데이터 전처리

### 참고자료(선택)

- 운영 서버에 API 호출을 위해 토큰 발급받기
    
    [로그인](https://www.notion.so/e65ba0867a8d4857b859c659a1708709) 
    

### 궁금한점(선택)

> 프론트엔드 개발자님께 질문
> 
1. 홈 모달 거리값 & 지점 상세페이지 거리값이 제대로 표기되지 않는 문제
    - 현재: `지도 중심 좌표~지점 거리` 로 표기
    - 변경 요청: `현재 위치~지점 거리` 로 표기
2. 지점 상세페이지 URL의 파라미터 userLat, userLng(사용자 현재 위치 위도, 경도)값으로 인해 A 사용자가 B 사용자에게 링크를 공유했을 때 표기되는 거리값이 고정되는 문제
    - 현재 URL: [https://photosmap.vercel.app/shopDetail?shopId=19024&userLat=37.51181455026082&userLng=127.10930701718534](https://photosmap.vercel.app/shopDetail?shopId=19024&userLat=37.51181455026082&userLng=127.10930701718534)
    - 변경 요청 URL: [https://photosmap.vercel.app/shopDetail?shopId=19024](https://photosmap.vercel.app/shopDetail?shopId=19024)
        - 사용자 현재 위치의 위도, 경도 → URL에 노출하지 않고 지점 상세 조회 API 호출시 내부적으로 사용
