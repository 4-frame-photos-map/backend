## 주민지

### 체크리스트(필수)
- **refactor** : Shop 도메인
   - [x]  응답 msg 통일 및 공통 내용 추출하여 상수로 생성
   - [x] @ NotNull 유효성 검증 어노테이션 올바르게 적용
       - 키워드 검색, 브랜드 검색 요청 DTO 위도/경도 자료형 Wrapper로 변경 
   - [x] 카카오 API 호출 로직 중복 제거
      - 키워드검색, 현재위치 기준 브랜드검색, 현재위치 기준 전체 검색 **공통 메서드** 사용하도록 수정
      - 카카오 응답 DTO 통일
        - 키워드검색은 phone 추가로 적용, 브랜드검색은 페이징 요청파라미터 제거
- **refactor** : 키워드 검색
   - [x] Service -> Controller 단으로 예외처리 이동
   - [x] 반환데이터가 없을 시 404 대신 200 처리
   - [ ] 키워드 유효성 검사 (유도한 키워드가 맞는지)
- **refactor** : 현재위치 기준 브랜드 검색
   - [x] 2km 반경 제한 추가
   - [x] 브랜드 파라미터 유효성 검사 -> 대표브랜드(인생네컷, 포토이즘박스, 포토그레이, 하루필름) 여부 체크
- **refactor** : 현재위치 기준 전체 검색
   - [x] 브랜드별 검색에서 전체 검색으로 로직 수정
### 참고자료(선택)
#### @NotNull Validation이 숫자형 필드에 제대로 적용되지 않은 문제 
- Primitive Type은 Object가 아니라 기본적으로 null 체크 불가능
- 따라서 숫자형 필드 null 체크를 하려면 Wrapper 클래스로 선언했어야 하는데 Primitive 타입으로 잘못 선언
- Primitive Type을 Wrapper Class로 수정하여 해결
  - `Case 1` double + @NotNull 사용 X 
    <img width="750" alt="Pasted Graphic 8" src="https://user-images.githubusercontent.com/63441091/226995954-2e7a7207-6275-4d0e-8c6b-3d34b48aaba2.png">
  
  - `Case 2` double + @NotNull 사용 O
    - @NotNull을 사용하지 않았을 때와 동일하게 타입 변환을 하면서 오류가 발생합니다
    - null 체크가 아닌 Empty String("") 체크가 됐습니다 (double은 null 체크가 불가능하기 때문입니다)
    <img width="750" alt="Pasted Graphic 6" src="https://user-images.githubusercontent.com/63441091/226997882-0fd655ad-eafe-4e98-ae42-f2f9a0d76e8c.png">


  - `Case 3` Dobule + @NotNull 사용 O
    - @NotNull로 유효성 검사가 제대로 이루어지는 것을 확인할 수 있습니다
   <img width="750" alt="Pasted Graphic 7" src="https://user-images.githubusercontent.com/63441091/226995833-78112c52-4287-4741-97fa-1d5492384cd1.png">

### 의논사항
- Marker, List 페이지에서  카카오 API 호출 시 응답에 phone 필드 포함 여부
  - `회의 결과` phone을 노출시키고 싶다면 상세 페이지에서만 응답받도록
### 다음주 계획(선택)
- **refactor** : 키워드 검색
   - 키워드 유효성 검사 (유도한 키워드가 맞는지)
- **refactor** : 현재위치 기준 검색
   - 브랜드 검색(기존 `/brand`)과 전체 검색(기존 `/marker`) 로직 합치기
 - **refactor** : 상세페이지
   - 통일된 응답구조 반영
   - 카카오 api 호출하여 장소명 받아오기
- **refactor** : JsonNaming Snake Case 스프링 설정에 추가 
    - Shop 도메인 카카오 통신 후 역직렬화에 문제 없는지 테스트
