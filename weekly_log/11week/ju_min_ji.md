## 주민지
### 체크리스트 (필수)
- **refactor** : Favorite
  - [x] 찜 목록 정렬 기준 placename 제거, sort 파라미터 제거
  - [x] 찜 여부 - 로그인 시 찜이 되어있을 때(true), 찜이 되어있지 않을 때(false) / 비로그인시 항상 false로 수정
- **refactor** : Shop
   - [x] 카카오 API 호출 메서드에서 429 에러 발생 시 여분 API KEY(3개) 유연하게 사용할 수 있도록 수정하여 쿼터 분배
   - [x] Shop 파라미터 ModelAttribute에서 RequestParam으로 수정하여 **유니크한 공통 에러 코드 적용**
       - NotBlank, NotEmpty -> `002`(파라미터 타입 에러)
       - NotNull -> `003`(파라미터 누락)
       - 깃북에 파라미터 커스텀 에러코드 반영 완료
   - [x] **키워드 조회, 브랜드별 조회, 찜 목록 조회, 상세조회 정확도 개선**
      - 카카오맵에서 제공하는 도로명주소는 호수나 층까지 제공하지 않기 때문에 한 건물 번호 내에 2개의 포토부스가 존재하는 경우 도로명주소는 유니크한 값이 될 수 없음
      - 따라서 DB 장소명이 카카오 API 브랜드명 포함하는지 검사하도록 쿼리조건으로 추가
      - 현재 DB에 저장되어 있는 도로명주소 중복 데이터는 총 42개(경기 23개, 서울 4개, 대전 1개, 부산 4개, 세종 1개, 울산 2개, 인천 3개, 전북 2개, 충북 2개)
      - 그러나 위 데이터 중 브랜드명까지 중복되는 데이터는 없으므로, 중복데이터로 인한 오류 가능성 X
- **DB 작업**
   - [x] Shop 테이블 brand_id 인덱스로 추가
   - [x] 즉석사진관이 아닌 row 삭제
      - 모든 즉석사진 브랜드로 포함여부 검사하여 포함되지 않을 시 삭제
   - [x] DB 공공데이터 추가
      - 경기, 강원, 경북, 경남, 광주, 대구, 대전, 부산, 세종, 울산, 인천, 전남, 제주, 충남, 충북
   - [x] **도로명주소 중복 데이터 체크하여 발생 가능한 오류 API 테스트 및 오류 가능성 제거**
     - 도로명주소, 장소명 일치하는 데이터 1개 제거(포토이즘박스 송도가로수길)
     - 도로명주소 중복데이터 중 포토이즘스튜디오와 같이 즉석사진이 아닌 일반 사진관에 해당하는 데이터 제거
### 참고 자료 (선택)
#### JSON 파일 Shop 테이블에 import 후 필수작업
```sql
-- [JSON 파일 Shop 테이블에 import 후 필수작업 1] 즉석사진이 아닌 업종 제거하기
DELETE FROM shop WHERE place_name not like '%인생네컷%'
                   AND place_name not like '%그믐달%'
                   AND place_name not like '%나랑한컷%'
                   AND place_name not like '%비룸%'
                   AND place_name not like '%모노맨션%'
                   AND place_name not like '%셀픽스%'
                   AND place_name not like '%썸컷%'
                   AND place_name not like '%스냅치즈%'
                   AND place_name not like '%스위치%'
                   AND place_name not like '%시현하다%'
                   AND place_name not like '%인생사진%'
                   AND place_name not like '%인스포토%'
                   AND place_name not like '%인싸포토%'
                   AND place_name not like '%추억사진관%'
                   AND place_name not like '%포토그레이%'
                   AND place_name not like '%포토스트리트%'
                   AND place_name not like '%포토아이브%'
                   AND place_name not like '%포토드링크%'
                   AND place_name not like '%포토랩플러스%'
                   AND place_name not like '%포토매틱%'
                   AND place_name not like '%포토시그니처%'
                   AND place_name not like '%포토이즘%'
                   AND place_name not like '%포토인더박스%'
                   AND place_name not like '%포토카드%'
                   AND place_name not like '%포토하임%'
                   AND place_name not like '%폴라스튜디오%'
                   AND place_name not like '%플립폴리%'
                   AND place_name not like '%플레이인더박스%'
                   AND place_name not like '%하루필름%'
                   AND place_name not like '%해리포토%'
                   AND place_name not like '%홍대네컷%';

-- [JSON 파일 Shop 테이블에 import 후 필수작업 2] create_date, modify_date, favorite_cnt 초기화하기
UPDATE shop SET create_date = Now() WHERE create_date IS NULL;
UPDATE shop SET modify_date = Now() WHERE modify_date IS NULL;
UPDATE shop SET favorite_cnt = 0 WHERE favorite_cnt IS NULL;
UPDATE shop SET star_rating_avg = 0 WHERE star_rating_avg IS NULL;
UPDATE shop SET review_cnt = 0 WHERE review_cnt IS NULL;

-- [JSON 파일 Shop 테이블에 import 후 필수작업 3] 카카오맵과 비교가능한 지역명으로 수정하기 (아래 지역 이외 지역들은 엑셀로 미리 가공, 세종특별자치시와 제주특별자치시는 카카오맵에 풀네임으로 존재)
UPDATE shop SET road_address_name= REPLACE(road_address_name,'서울특별시','서울');
UPDATE shop SET road_address_name= REPLACE(road_address_name,'경기도','경기');
UPDATE shop SET road_address_name= REPLACE(road_address_name,'강원도','강원');
UPDATE shop SET road_address_name= REPLACE(road_address_name,'경상북도','경북');

-- [JSON 파일 Shop 테이블에 import 후 필수작업 4] brand_id 설정하기
UPDATE shop SET brand_id = 1 WHERE place_name LIKE '%인생네컷%';
UPDATE shop SET brand_id = 2 WHERE place_name LIKE '%하루필름%';
UPDATE shop SET brand_id = 3 WHERE place_name LIKE '%포토이즘%';
UPDATE shop SET brand_id = 4 WHERE place_name LIKE '%포토그레이%';
UPDATE shop SET brand_id = 5 WHERE place_name NOT LIKE '%인생네컷%'
                               AND place_name NOT LIKE '%하루필름%'
                               AND place_name NOT LIKE '%포토이즘%'
                               AND place_name NOT LIKE '%포토그레이%';


```
### 다음주 계획 (선택)
- ShopTitle 
  - 부여 기준(각 달을 기준으로 찜 수, 청결도 측정) 수정 및 추가 
  - 매월 1일에 전체 삭제 후 이 달의 기준에 맞춰 부여할 수 있도록 스케줄링
- Shop Test 리팩토링
- Shop 테이블에 road_address_name, place_name 복합 인덱스로 추가하기에 적절한 케이스인지 자료조사(단일 인덱스 케이스와 비교)
