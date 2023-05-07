## 주민지

### 체크리스트(필수)

- Crawl
    - [x] `feat` : 인스포토, 포토드링크, 포토랩플러스, 해리포토, 플레이인더박스 지점명 및 주소 크롤링, 데이터 전처리
    - [x] `feat` : 셀픽스, 포토스트리트 지점명 크롤링, 데이터 전처리

- Shop
   - [x] `refactor` : DB Shop vs Kakao API Shop 비교 로직 수정
      - DB 데이터가 공공 데이터에서 크롤링 데이터로 바뀌었으므로 크롤링 데이터 특성(지점명, 주소 정확도)에 맞게 수정
   - [x] `refactor` : 카카오맵 API 조회 결과 필터링 카테고리명 `즉석사진` 대신 `사진`으로 변경
     - 즉석사진(셀프포토부스)인데 카카오맵 API에서는 사진관,포토스튜디오 카테고리에 분류되어있는 경우 있음 
        - e.g. 인스포토, 셀픽스
     - 조회 키워드가 광범위해져도 DB 데이터는 모두 즉석사진 브랜드이므로 사용자에게는 즉석사진만 반환하게 됨
  - [x] `refactor` : Shop 테이블의 주소 컬럼 roadAddressName에서 지번 주소도 포괄하는 `address`로 변경, 관련 코드 수정
  - [x] `refactor` : 일관성을 위해 장소명은 카카오 API 장소명이 아닌 DB 장소명으로 통일하여 응답
  - [x] `refactor` : 사용자 중심좌표와 지도 중심좌표가 같지 않을 때만 지점으로부터 거리 재계산
  - [x] `refactor` : KakaoMaps API 데이터와 불일치로 조회불가능한 shop-id 확인 후 조치 취할 수 있도록 Redis에 캐시
  - [x] `refactor` : 수정된 사항 반영하여 ControllerTests 리팩토링


- Favorite
   - [x] `refactor` : 찜 추가 중복 검사 시 id 존재여부만 체크하도록 `findBy` 대신 `existsBy`로 수정
   - [x] `refactor` : 찜 목록 조회 API 요청 처리 시 `address`가 null이면 기존처럼 [Kakao API - 키워드로 장소 검색](https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword) 호출, null이 아니면 [Kakao API - 주소 검색하기](https://developers.kakao.com/docs/latest/ko/local/dev-guide#address-coord) 호출하도록 수정하여 Kakao API 호출 API 쿼터 분배

- 키워드 조회 API, 반경 2km 내 브랜드 별 조회 API
   - [x] `refactor` : DB 데이터와 일치하는 데이터일 시 `카카오맵 바로가기 Url`, `지점 위도/경도` shop-id로 캐시
   - [x] `refactor` : 홈 탭과 내 주변 탭의 반경값을 분리하기 위해 브랜드 별 조회 API 반경 파라미터 추가하여 반경 값 유연하게 조정하기 
   - [x] `refactor` : 반경 내 브랜드별 조회 API **응답 정렬 기준** 수정
     - 수정 전 : 지도 중심좌표로부터의 지점까지의 거리를 기준으로 거리순 정렬 (즉 KakaoMaps API가 제공하는 정렬 사용)
     - 수정 후 : 사용자 현재위치로부터의 지점까지의 거리를 기준으로 거리순 정렬 (자체 정렬)

- 찜 목록 조회 API, 상세조회 API
   - [x] `refactor` : 카카오 API 호출 전에 캐시된 데이터 있는지 확인하여 없으면 기존 로직대로 응답 반환, 있으면 자체적으로 거리값 계산하여 반환
   - [x] `refactor` : 상세조회 API에서 거리값 정확성을 위해 거리 파라미터 제거 후 사용자 위도, 경도로 직접 계산하도록 수정

### 참고자료(선택)

#### I. 크롤링 이후 개선 지점
- **문제 지점** : 공공데이터로부터 수집한 장소명이 부정확하고 일관성이 없었기 때문에, 사용자에게 정돈된 장소명을 보여주기 위해서 DB 장소명을 사용하지 못하고 매번 카카오 API를 호출하여 프론트에 응답. 특히 찜 목록과 리뷰 목록에서는 개수에 비례하여 호출해야 됐기 때문에 외부 API 호출이 상당히 비효율적으로 발생.
- **해결 방법** : 공공데이터를 대신하여 공식 사이트 크롤링을 통해 데이터를 수집하여 DB 데이터를 초기화
- **개선점** 
   - 1) **DB 장소명 표준화** : 정돈된 장소명만을 위해 카카오 API 호출해야 했던 리뷰 목록에서 외부 API 호출 비용 절감
   - 2) **유지보수 용이성 증대** : 기존 CSV 파일(공공데이터)과 달리 크롤링 데이터를 DB에 저장하는 방식은 데이터 전처리 과정 등 유지보수하기 용이
   - 3) **데이터 양적 확장으로 인한 서비스 개선** : 더 많은 데이터를 수집하여 다양한 지점 정보를 제공함으로써, 서비스의 품질 개선

#### II. 크롤링 이후 바뀐 Shop 조회, 비교 로직
- [우선순위 1] 장소명 일치여부 or 도로명주소 포함여부
   - `db.roadAddressName.contains(kakako.roadAddressName)`
   - 카카오 API 주소에는 상세주소가 포함되어 있지 않지만, DB 주소는 상세주소도 포함된 경우가 있으므로
- [우선순위 2] 장소명 일치여부 or 지번주소 포함여부
   - `db.roadAddressName.contains(kakao.addressName)`
   - DB 주소가 지번 주소인 경우도 있으므로
- 중복데이터 발생 시 **주소로 인한 중복**일 확률이 매우 높고 지점명까지 붙은 장소명은 고유성이 강하므로
   - [우선순위 1] 장소명 일치여부
   - [우선순위 2] DB 장소명이 Kakao API 장소명에 포함된 브랜드명을 포함여부

#### III. [크롤링 데이터 DB 저장 후 필수작업] 카카오 API 데이터와 비교가능한 지역명으로 수정
- cf. 카카오 API 주소명에서 '세종특별자치시'와 '제주특별자치도'는 줄여서 사용하지 않음
```sql
-- data.sql

UPDATE shop SET address = REPLACE(address,'서울특별시','서울');
UPDATE shop SET address= REPLACE(address, '서울시', '서울');
UPDATE shop SET address= REPLACE(address, '서울특벼릿', '서울');
UPDATE shop SET address= REPLACE(address, '부산광역시','부산');
UPDATE shop SET address= REPLACE(address,'대구시','대구');
UPDATE shop SET address= REPLACE(address,'대구광역시','대구');
UPDATE shop SET address= REPLACE(address,'인천시','인천');
UPDATE shop SET address= REPLACE(address,'인천광역시','인천');
UPDATE shop SET address= REPLACE(address,'광주광역시','광주');
UPDATE shop SET address= REPLACE(address,'대전시','대전');
UPDATE shop SET address= REPLACE(address,'대전광역시','대전');
UPDATE shop SET address= REPLACE(address,'울산광역시','울산');
UPDATE shop SET address= REPLACE(address,'경기도','경기');
UPDATE shop SET address= REPLACE(address,'강원도','강원');
UPDATE shop SET address= REPLACE(address,'충청북도','충북');
UPDATE shop SET address= REPLACE(address,'충청남도','충남');
UPDATE shop SET address= REPLACE(address,'전라북도','전북');
UPDATE shop SET address= REPLACE(address,'전라남도','전남');
UPDATE shop SET address= REPLACE(address,'경상북도','경북');
UPDATE shop SET address= REPLACE(address,'경상남도','경남');
```
#### IV. 로컬 테스트 전 Shop 테이블 주소 컬럼명 변경하여 테스트
```sql
ALTER TABLE shop RENAME COLUMN road_address_name TO address;
```
    

### 궁금한점(선택)

- Q. Selenium Webdriver을 통해 Javascript 기반 코드 실행하여 동적 크롤링이 필요한 나머지 기타브랜드도 크롤링 할 지, 말 지 의견 공유 필요. 
- 팀원들 A. 동적 크롤링이 필요한 브랜드 데이터들이 합쳐봤자 많지 않고, 현재 크롤링 데이터만으로 충분한 것 같다.
