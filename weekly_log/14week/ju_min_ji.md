## 주민지

### 체크리스트(필수)

- Shop
  - [x] `fix` : 상세조회 API 사용자 현재위치 파라미터가 존재하지 않을 때는 거리값 미표시 ([#123](https://github.com/4-frame-photos-map/backend/issues/123))
  - [x] `refactor` : Kakao Maps API Web Client로 호출 시 발생하는 예외 처리 개선 ([#123](https://github.com/4-frame-photos-map/backend/issues/123))
      - 첫 번째 try-catch문 이후 429 상태 코드일 때만 나머지 try-catch 문을 처리하고(여분의 API KEY에 사용 가능 쿼터 존재하면 할당, 쿼터 한도 다 찼으면 예외처리), 그렇지 않은 경우에는 예외 처리를 하지 않고 로그만 출력
  - [x] `fix` : 브랜드 별 조회 API 정렬 기준 올바르게 수정 ([#123](https://github.com/4-frame-photos-map/backend/issues/123))
  - [x] `refactor` : 상세조회 시 발견하는 유효하지 않은 shop-id 저장 자료구조 Strings 타입에서 Set으로 변경 ([#123](https://github.com/4-frame-photos-map/backend/issues/123))
  - [x] `fix` : Kakao Maps API 데이터로 DB 조회시 공백 제거하여 비교 작업 정확도 개선 ([#126](https://github.com/4-frame-photos-map/backend/issues/126))

- Crawler ([#123](https://github.com/4-frame-photos-map/backend/issues/123)) 
  - [x] `fix` : Jsoup 503 에러로 인해 메인 브랜드 크롤링 API 분리 

- ShopTitle ([#130](https://github.com/4-frame-photos-map/backend/issues/130))
  - [x] `feat`: 지점 칭호 테스트 데이터 추가 
  - [x] `feat` : Shop API 응답에 지점 칭호 필드 추가 
  - [x] `feat`: 모든 지점 칭호 로그 조회하는 엔드포인트 추가 
  - [x] `fix` : 지점 칭호 단건 조회 시 id 유효성 체크하도록 수정, 칭호 존재하지 않으면 빈 리스트 반환
  - [x] `refactor` : ShopTitleLogController 통일된 응답 구조로 수정 및 지점 칭호 관련 미사용 코드 정리 
  - [x] `fix` :  ([#136](https://github.com/4-frame-photos-map/backend/issues/136))

### 다음주 계획(선택)
- ShopTitle
   - `fix` : 지점 칭호 id순으로 부여하도록 수정
   - `fix` : 테스트 데이터 재생성
- Shop
   - `fix` : 일반 사진관으로 분류된 즉석사진 데이터와 DB 주소값이 Null인 데이터 검색 결과에 포함되지 않는 문제 해결
   - `refactor` : Kakao Maps API 조회 결과 사이즈 확장 
