## 주민지

### 체크리스트(필수)
- ShopTitle
  - [x] `fix` : 지점 칭호 id순으로 순차적으로 부여하도록 수정 ([#136](https://github.com/4-frame-photos-map/backend/issues/136))
  - [x] `fix` : 테스트 데이터 재생성 ([#136](https://github.com/4-frame-photos-map/backend/issues/136))

- Shop
  - [x] `fix` : 일반 사진관으로 분류된 즉석사진 데이터와 DB 주소값이 Null인 데이터 검색 결과에 포함되지 않는 문제 해결 ([#137](https://github.com/4-frame-photos-map/backend/issues/137))
  - [x] `refactor` : Kakao Maps 키워드로 장소검색하기 API에서 반환되는 결과 노출 개수 증가시키기 ([#137](https://github.com/4-frame-photos-map/backend/issues/137))
  - [x] `fix` :  Kakao Maps API 데이터로 DB 조회하는 과정에서 중복 결과 발생 시 올바르게 처리하도록 로직 수정 ([#144](https://github.com/4-frame-photos-map/backend/issues/144))
  - [x] `fix` : Kakao Maps API 데이터로 DB 조회한 후 장소명으로 추가 필터링하도록 코드 수정 ([#155](https://github.com/4-frame-photos-map/backend/issues/155))
  - [x] `refactor` : 미사용 응답 필드 및 API 제거 작업 ([#151](https://github.com/4-frame-photos-map/backend/issues/151))


### 참고사항(선택)
- JUnit5 참고자료
   -  [AssertJ 자주 사용하는 문법 (튜플 추출하여 검사하는 문법 포함)](https://umanking.github.io/2021/06/26/assertj-iteration/)
   -  [JUnit5 & AssertJ & MockMVC 기본 사용법](https://velog.io/@tjseocld/JUnit5-%EA%B8%B0%EB%B3%B8-%EC%82%AC%EC%9A%A9%EB%B2%95#assertj-%EB%9E%80)
- 이전에 공공데이터를 사용할 때와 달리 조회된 모든 데이터가 카카오 응답 데이터와 장소명이 일치하지 않는 경우, 추가로 브랜드명을 포함하는 데이터가 존재하는지 검사하여 반환하지 않는 이유
  - [#126](https://github.com/4-frame-photos-map/backend/issues/126)에서 언급한 `인생네컷 춘천 터미널로드점`, `인생네컷 춘천 명동로드점`, `인생네컷 춘천 CGV로드점`처럼 동일한 주소 내에 동일한 브랜드의 여러 지점이 존재할 수 있기 때문에, 단순히 브랜드와 주소가 일치하는 데이터만으로 동일한 지점임을 판단하기에는 근거가 부족하기 때문입니다.

### 다음주 계획(선택)
- Shop
  - `refactor` : 장소명으로 추가 필터링하는 로직에서 중복 제거
  - `refactor` : 도로명주소로 비교 후, 반환하는 지점 없을 시 지번주소로 DB 조회 재요청 보내는 로직에서 지번주소까지 조건에 추가하여 한 번에 요청보내기
    - 수정 후 기존 코드와 수행 시간 비교
  - `test` : FavoriteServiceTests 리팩토링
  - `feat` : 약관 CRUD API 추가
