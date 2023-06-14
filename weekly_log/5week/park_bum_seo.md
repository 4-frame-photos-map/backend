# 할 일

### 체크리스트(필수)

- [x]  ShopTitleLog
    - [x]  Entity 작성
    - [x]  Api
        - [x]  ShopTitleLog 칭호 조회
        - [x]  ShopTitleLog 칭호 추가
        - [x]  ShopTitleLog 칭호 삭제
    - [x]  ShopTitle 테스트 코드
        - Controller
        - Service
            - [x]  ShopTitleLog `조회, 성공한 경우`
            - [x]  ShopTitleLog `조회 시, 실패한 경우 예외를 발생시킨다.`
            - [x]  ShopTitleLog `추가`
            - [x]  ShopTitleLog `삭제`
- [x]  ShopTitle
    - [x]  Entity 작성
    - 칭호 종류 및 부여 기준
        
        
        | 이름 | 조건 | 내용 |
        | --- | --- | --- |
        | 핫 플레이스 | 찜 수 5개 이상 | 사람들이 주로 이용하는 포토부스에요. |
        | 청결 양호 | 청결 점수 4점 이상 | 시설이 깔끔해요. |
        | 보정 양호 | 보정 점수 4점 이상 | 사진이 잘 나와요. |
        | 소품 양호 | 소품 점수 4점 이상 | 다양하게 연출하기 좋아요. |
    
- [x]  Shop 마커 검색, 상세 조회 등 ShopTitle 데이터 포함

### 다음주 계획(선택)

1. NCP 배포 테스트
2. 찜
    - 스케쥴링 로직으로 변경
    - DB에 저장 시, enum String
