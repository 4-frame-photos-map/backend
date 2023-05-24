## 한승연

### 체크리스트(필수)

- member-title
    - [x]  `test` : 획득하지 않은 칭호 조회 테스트 코드 작성
    - [x]  `test` : 획득하고 대표 칭호로 설정된 칭호 조회 테스트 코드 작성
    - [x]  `test` : 획득하고 대표 칭호로 설정되지 않은 칭호 조회 테스트 코드 작성
    - [x]  `test` : 존재하지 않는 칭호 조회 실패 테스트 코드 작성
    - [x]  `#1` `refactor` : 회원이 보유한 칭호 단건 조회 쿼리 개선
    - [x]  `fix` : 회원 대표 칭호 설정 존재하지 않는 칭호, 중복된 대표 칭호 예외 처리 추가
    - [x]  `feat` : MemberTitle의 imageUrl을 colorImageUrl, bwImageURl로 분리, 칭호 획득 여부에 따라 컬러/흑백 이미지로 응답
- infra
    - [x]  `#2` RDS 생성 및 DB 데이터 이전 작업
    - [x]  AWS S3 회원 칭호 5개(컬러/흑백) 이미지 업로드, DB 저장

### 참고자료(선택)

- `#1` https://github.com/4-frame-photos-map/backend/issues/131
- `#2` [[MySQL] MySQL Server 이전하기 - Workbench로 export/import](https://www.notion.so/MySQL-MySQL-Server-Workbench-export-import-7183e3e8ac424455859dad3246d49145)

### 궁금한점(선택)

- 민지님
    - [x]  현재 카카오 키워드로 검색 API를 호출하여 최대 30개의 데이터를 가져오고 있나요?
        - 네
    - [x]  동일한 지점이 여러개 마커 표시되는 문제가 있습니다.
        1. 문제 상황: 같은 지점명을 가진 지점이 다른 위도/경도로 2개 이상 응답되어 중복으로 마커 표시되는 문제
            1. DB에는 `하루필름 광주 충장로점` 데이터 1개 존재 → 2개 표시
                
                ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/233edc17-78e5-47b8-8404-f94768b80f7f/Untitled.png)
                
            2. DB에는 `인생네컷 서울 롯데월드직영점` 데이터 1개 존재 → 3개 표시
                
                ![스크린샷 2023-05-22 오후 11.36.26.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fa721666-e191-4e93-a247-9593607574cf/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-05-22_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_11.36.26.png)
                
        2. 원인: `지점명 or 주소` 로 비교했을 때 매칭된 지점이 1개인 경우, 주소는 같은데 다른 지점명을 가진 데이터가 매칭되는 문제가 발생할 수 있음
            1. 첫번째 매칭된 경우
                
                
                |  | 지점명 | 주소 |
                | --- | --- | --- |
                | 카카오 API 데이터 | 하루필름 광주 충장로점 | 광주 동구 중앙로160번길 31-6 |
                | DB 데이터 | 하루필름 광주 충장로점 | 광주 동구 중앙로160번길 31-6 |
            2. 두번째 매칭된 경우 → 실제로는 매칭되면 안됨
                
                
                |  | 지점명 | 주소 |
                | --- | --- | --- |
                | 카카오 API 데이터 | 샤인메이크업스튜디오 | 광주 동구 중앙로160번길 31-6 |
                | DB 데이터 | 하루필름 광주 충장로점 | 광주 동구 중앙로160번길 31-6 |
                
                ![스크린샷 2023-05-23 오후 12.10.39.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6b475e4d-c601-4b0c-947e-983341f2430f/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-05-23_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_12.10.39.png)
                
        3. 해결방법
            1. **DB에서 조회된 지점이 2개이상일 때와 마찬가지로 지점명으로 한번 더 비교하기 → 채택**
            2. ~~DB에서 address 가 NULL인 데이터를 삭제하고 `findByPlaceNameAndAddressIgnoringSpace()` 으로 가져오기~~
    - [x]  도로명 주소와 지번 주소 비교를 위해 최대 2번 발생하는 쿼리를 1번으로 줄이는 방향으로 개선하며 어떨까요?
        - `findByPlaceNameOrAddressIgnoringSpace(지점명, 주소)` 총 2번
            - `findByPlaceNameOrAddressIgnoringSpace(지점명, 도로명주소)` 1번
            - `findByPlaceNameOrAddressIgnoringSpace(지점명, 지점주소)` 1번
            
            ```java
            @Query(
            "SELECT s
            FROM Shop s
            WHERE (FUNCTION('REPLACE', s.placeName, ' ', '') = :placeName)
            OR (FUNCTION('REPLACE', s.address, ' ', '') LIKE %:address%)")
            List<Shop> findByPlaceNameOrAddressIgnoringSpace(
            	@Param("placeName") String placeName,
            	@Param("address") String address);
            ```
            
        - `findByPlaceNameOrAddressIgnoringSpace(지점명, 도로명주소, 지번주소)` 1번
            
            ```java
            @Query(
            "SELECT s 
            FROM Shop s 
            WHERE (FUNCTION('REPLACE', s.placeName, ' ', '') = :placeName)
            OR (FUNCTION('REPLACE', s.address, ' ', '') LIKE %:address1%)
            OR (FUNCTION('REPLACE', s.address, ' ', '') LIKE %:address2%)")
            List<Shop> findByPlaceNameOrAddressIgnoringSpace(
            	@Param("placeName") String placeName,
            	@Param("address1") String address1, 
            	@Param("address2") String address2);
            ```
            

### 다음주 계획(선택)

- member-title
    - [ ]  `test` : 칭호 전체 조회 테스트 코드 작성
    - [ ]  `test` : 대표칭호 설정 성공/실패 테스트 코드 작성
- member
    - [ ]  `test` : 테스트 코드 작성
