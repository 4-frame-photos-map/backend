## 박범서

### 체크리스트(필수)

- [ ]  ShopTitleLog
    - [x]  Entity 작성
    - [ ]  Api
        - [x]  ShopTitleLog 칭호 조회
        - [x]  ShopTitleLog 칭호 추가
        - [ ]  ShopTitleLog 칭호 삭제
    - [ ]  ShopTitle 테스트 코드
        - Controller
        - Service
            - [x]  ShopTitleLog `조회, 성공한 경우`
            - [x]  ShopTitleLog `조회 시, 실패한 경우 예외를 발생시킨다.`
            - [x]  ShopTitleLog `추가`
            - [ ]  ShopTitleLog `삭제`
- [x]  ShopTitle
    - [x]  Entity 작성
    - 칭호 종류 및 부여 기준
        
        
        | 이름 | 조건 | 내용 |
        | --- | --- | --- |
        | 핫 플레이스 | 찜 수 5개 이상 | 사람들이 주로 이용하는 포토부스에요. |
        | 청결 양호 | 청결 점수 4점 이상 | 시설이 깔끔해요. |
        | 보정 양호 | 보정 점수 4점 이상 | 사진이 잘 나와요. |
        | 소품 양호 | 소품 점수 4점 이상 | 다양하게 연출하기 좋아요. |
    
- [ ]  Shop 마커 검색, 상세 조회 등 ShopTitle 데이터 포함

### 참고자료(선택)

- data.sql
    - [참고 사이트](dbpia.co.kr/journal/articleDetail?nodeId=NODE10582867)
    
    

### 에러 해결

- **컬럼명 condition 에러**
    - 해당 데이터베이스 keyword를 테이블명이나, 컬럼명으로 사용할 때 생기는 에러
    - 해결 방법 : 백틱 사용
        - **일반적으로 이러한 키워드를 데이터베이스, 테이블 또는 열 이름으로 사용하지 않는다.**
        
        ```sql
        select **like** from table1; (X,like는 예약어 이므로 에러 발생)
        
        select **`like`** from table1; (O, 백틱 사용)
        ```
        
    - 참고
    
    [MySQL 5.7 Reserved Words - IONOS Help](https://www.ionos.com/help/hosting/using-mysql-databases-for-web-projects/mysql-57-reserved-words/)
    
    [MySQL 예약어 - 제타위키](https://zetawiki.com/wiki/MySQL_%EC%98%88%EC%95%BD%EC%96%B4)
    
- Optional **null**
    - **컬렉션**의 경우에는 결과가 없다면 빈 컬렉션 반환
    - **단건 조회**의 경우에는 결과가 없다면 null반환, 결과가 2건 이상이라면javax.persistence.NonUniqueResultException 예외
     발생
    - [https://kingchan223.tistory.com/345](https://kingchan223.tistory.com/345)

### 궁금한점(선택)

- shopTitle 같은 경우는 패키지 명명에 어떻게 써야할지

### 다음주 계획(선택)
- ShopTitleLog API 완료
- ShopTitleLog 테스트 코드 완료
- 포토부스 칭호 조건 체크
- 승연님 left outer join이 난 경우와 본인 경우 비교해보기 (어떤 차이점이 있는지 )
- cascade, orphan → 무조건 해주는게 좋은지 고민해보기 ( 부모, 자식 삭제 )
