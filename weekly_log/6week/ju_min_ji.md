## 주민지

### 체크리스트(필수)
- **feat** : Shop 초기데이터 생성
    - [x] 소상공인 공공데이터 CSV 파일 엑셀에서 필요한 데이터 가공 후 가공된 데이터만 따로 `utf-8(쉼표로 구분)`로 CSV 저장
    - [x] CSV -> JSON 변환
    - [x] JSON 파일 MySQL에 import 및 스프링 설정 변경
    - [x] 생성날짜, 수정날짜, 찜 수 null값 처리(SQL update)
    - [x] 카카오 API데이터와 DB Shop 데이터 비교로직 수정
        - **장소명 비교에서 도로명주소 비교로 수정**
        - DB 도로명주소에서 서울특별시 -> 서울 변환 처리: 카카오 API 데이터와 비교 위해
    - [ ] DB `brand` 컬럼 제거 후 브랜드 검색시 `findByBrand` 대신 `like`나 `startingWith` 메서드로 수정
- **refactor** : 키워드로 Shop 검색 거리순 정렬 추가
   - [x] 요청 DTO에 위도, 경도 추가
   - [x] kakao API 요청 파라미터에 x, y, sort(->distance) 추가
   - [ ] 응답 DTO에 distance 추가

### 참고자료(선택)
#### I. 키워드 검색 거리순 정렬 도입 후 문제
- **문제 :** 거리순 정렬을 도입하지 않았을 때보다 조회 결과가 적게 리턴 
  - `키워드 검색` 기능 실용성 측면에서 논의 필요
- **회의 결과 :**  __거리순 정렬, 반경 제한은 하지 않고 현재 위치 요청 후 현재위치로부터의 거리(distance)만 추가로 응답받기로 결정__
  - 현재 위치 기준으로 포토부스를 검색하고 싶다면, `브랜드 검색(현재 위치 기준 정렬)`이나 `반경 2km 이내 검색 기능`만으로 충분
  - 위 기능들과 달리 `키워드 검색`은 **현재 위치에 제한받지 않고 넓은 범위를 검색하고 싶을 때** 주로 사용될 것
  - 따라서 거리순 정렬보다는 최대한 많은 검색 결과가 나오는 **정확도순 정렬**이 해당 기능에 적합하다고 생각(sort 기준을 따로 지정하지 않으면 기본적으로 정확도순 정렬)
  - 상세페이지나 간단한 info 페이지를 위해 현재는 현재 위치로부터의 거리(distance)도 응답으로 보내기로 결정했으나, 상세페이지나 info 페이지에서도 한 번 더 카카오 api 호출하기로 결정되면 키워드검색 응답값 조정 필요

1. 거리순 정렬 도입 전
   - 요청 파라미터: 키워드
   - 응답 파라미터: 상점명, 도로주소명, 상점 위도 및 경도
<img width="800" alt="Pasted Graphic" src="https://user-images.githubusercontent.com/63441091/223683383-2b35b741-beaf-48d2-900a-965d66676447.png">

<br>

2. 거리순 정렬 도입 후
   - 요청 파라미터: 키워드, 현재위치 위도 및 경도, 거리순 정렬(sort=distance)
   - 응답 파라미터: 상점명, 도로주소명, 상점 위도 및 경도
<img width="800" alt="Pasted Graphic 1" src="https://user-images.githubusercontent.com/63441091/223683418-d91720d5-6135-422e-866a-c1b61b143b32.png">

#### II. CSV -> EXCEL -> CSV -> JSON으로 변환 이유
1. CSV -> EXCEL : **데이터 가공**
- 공공데이터에서 상호명과 지점명이 부정확한 경우 존재 엑셀 Concat 함수로 해결
- 공공데이터 도로명주소에서는 괄호형태로 참고항목까지 포함되어 있음 (동,건물명)
  - 카카오 API 주소 데이터에서는 동과 건물명 미포함
  - 도로명 주소로 API 데이터와 DB데이터 비교 위해 엑셀 Left, Find 함수로 (동, 건물명)은 제거
2. EXCEL -> CSV -> JSON : **인코딩 오류**
- 엑셀에서 변환한 CSV 파일로 mysql workbench에 바로 import 시 UTF-8 인코딩 변환 오류 발생
   - [StackOverFlow](https://stackoverflow.com/questions/71992180/mysql-error-unhandled-exception-ascii-codec-cant-decode-byte-0xef-in-positi) 방법들은 실패
   - [mac m1에서 발생하는 인코딩 오류](https://velog.io/@moojun3/MySQL-Unhandled-exception-ascii-codec-cant-decode-byte-mac) 참고 후 CSV -> JSON 파일로 재변환 **[문제 해결]**
```
Unhandled exception: 'ascii' codec can't decode byte 0xee in position 15: ordinal not in range(128) 
```
#### III. MySQL에 초기데이터 파일(JSON) import 방법
1. Spring SQL script(`data.sql`)에서 초기데이터 **Insert문 주석 처리**됐는지 확인
2. `application` 실행하여 초기데이터 없는지 확인
<img width="497" alt="Pasted Graphic" src="https://user-images.githubusercontent.com/63441091/223663597-661e402f-7fe4-4e53-80e7-878d67cb332d.png">
3-1. 스키마 선택 오른쪽 버튼 클릭 -> `table data import wizard`
<img width="497" alt="Load Spatial Data" src="https://user-images.githubusercontent.com/63441091/223662766-d7ec56db-985f-4f0c-b1d1-cd1699b9976a.png">

<br>

3-2. 공유한 json 파일 선택 <br> 
<img width="497" alt="Pasted Graphic 2" src="https://user-images.githubusercontent.com/63441091/223662795-136868b9-dcbc-457b-8002-294913746fb0.png">

<br>

3-3. `use existing table` 선택 후 shop 테이블 선택 <br>
<img width="497" alt="Pasted Graphic 3" src="https://user-images.githubusercontent.com/63441091/223662813-69624f99-a3c3-4266-99d2-3de60103ffa2.png">

3-4. 각 각 `place_name`과 `road_address_name`에 매치하는지 확인 <br>
<img width="497" alt="Pasted Graphic 4" src="https://user-images.githubusercontent.com/63441091/223662868-f7061182-7d3c-4b21-a59d-c3066b32bcd4.png">
<br>

3-5. DB에 INSERT 완료 <br>
<img width="497" alt="Pasted Graphic 5" src="https://user-images.githubusercontent.com/63441091/223662891-3a0e9e9c-71ea-4469-9cd5-fb199f5d1cac.png">

<br>

4. `jpa-hibernate-ddl-auto` **update**로 변경하여 import된 데이터 재사용가능하도록 설정됐는지 확인
5. Spring SQL script(`data.sql`)에서 **생성날짜, 수정날짜, 찜 수 초기화** 및 **서울특별시 -> 서울 수정**됐는지 확인
```sql
# data.sql

-- MySQL에 JSON 파일 Shop 테이블에 import 후 create_date, modify_date, favorite_cnt 초기화하기
UPDATE shop SET create_date = Now() WHERE create_date IS NULL;
UPDATE shop SET modify_date = Now() WHERE modify_date IS NULL;
UPDATE shop SET favorite_cnt = 0 WHERE favorite_cnt IS NULL;

-- Shop import 데이터에서 '서울특별시'를 '서울'로 변경
UPDATE shop SET road_address_name= REPLACE(road_address_name,'서울특별시','서울');
```

### 궁금한 점(선택)
- 프론트에서 Map Marker나 List 페이지의 응답 값(상점명, 현재위치로부터의 거리)을 저장했다가 다른 페이지(상세페이지, 간단 info페이지)로 보내주는 것이 가능한지?
  - 카카오 정책상 불가능하다면 상세페이지나 간단한 info페이지에서도 API 호출하도록 구현 필요

### 다음주 계획(선택)

- **feat** : 키워드 검색 Map Marker 기능 추가
- **refactor** : DB brand 컬럼 제거 후 브랜드 검색 로직 수정
- **refactor** : 응답 DTO 모두 Snake Case로 수정
