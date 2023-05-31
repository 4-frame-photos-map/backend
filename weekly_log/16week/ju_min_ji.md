## 주민지

### 체크리스트(필수)
- Shop
  - [x] `refactor` : 지점 비교 로직 중복 제거 및 불필요한 null 체크 제거 ([#157](https://github.com/4-frame-photos-map/backend/issues/157))
  - [x] `refactor` : 도로명주소와 지번주소를 OR 연산으로 한 번의 쿼리로 조회하도록 수정 ([#157](https://github.com/4-frame-photos-map/backend/issues/157)) 
  - [x] `refactor` :  장소명으로만 비교하는 경우 주소값이 null인 조건 추가하여, 조회 후 별도의 필터링 불필요하도록 수정 ([#157](https://github.com/4-frame-photos-map/backend/issues/157))
  - [x] `refactor` :  최소한의 블록만 읽도록 지점 비교 쿼리 결과 수 제한 ([#157](https://github.com/4-frame-photos-map/backend/issues/157))


### 참고사항(선택)
#### I. `두 번의 쿼리 요청`을 `한 번의 쿼리로 여러 조건을 OR 연산으로 조회`로 변경하여도 수행시간에 차이가 없음
   - DB 호출 횟수는 줄었지만, 처리 블록은 비슷하거나 더 많아서 수행시간에 영향을 주지 않음
   - 그럼에도 수정한 이유는 가독성과 코드 의도의 명확성을 높이기 위함

#### II. JPQL에서 LIMIT절 지원 여부
   - JPQL에서는 LIMIT절을 지원하지 않으므로, Pageable 인터페이스를 활용하거나 NativeQuery를 사용하여 LIMIT절을 활용해야 함
   - JPQL에서 Pageable 인터페이스 활용하여 결과 수 제한에 관한 내용은 [참고자료](https://codingcho.tistory.com/255) 확인

### 다음주 계획(선택)
- Favorite Service Tests 코드 리팩토링
- JPQL에서 Pageable 인터페이스 활용 시 여러 건을 조회한 후, 조건에 맞는 한 건을 반환하는 것인지 아니면 조건에 맞는 한 건을 조회하면 바로 반환하는 것인지 테스트
