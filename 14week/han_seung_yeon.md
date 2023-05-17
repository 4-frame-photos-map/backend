## 한승연

### 체크리스트(필수)

- member-title
    - [x]  `feat` : MemberTitle DB 테이블과 Entity에 이미지 URL 컬럼 추가, 제약 조건 추가
    - [x]  `feat` : 회원 칭호 전체 조회 API 응답에 획득 개수, 대표 칭호 정보(id, 이름, 획득 여부, 대표 칭호 여부, 이미지 URL), 칭호 정보(이미지 URL) 추가
    - [x]  `feat` : 회원 칭호 단건 조회 API 응답에 획득 여부, 대표 칭호 여부, 이미지 URL 추가
    - [x]  `refactor` : 회원 칭호 전체 조회 API 획득한 회원 칭호 조회 쿼리 개선, 획득한 칭호인지 검사하는 로직 개선
    - [x]  `docs` : 회원 칭호 단건, 전체 조회 API GitBook 수정
- faq
    - [x]  `docs` : 자주 묻는 질문(FAQ) 질문, 답변 카테고리 별로 정리하기
    - [x]  `feat` : Faq, Category DB 설계

### 참고자료(선택)

[[SpringBoot] 토큰 재발급 실패 문제](https://www.notion.so/SpringBoot-2ba1023ecb8844a1b3b703f9380e7035)

### 궁금한점(선택)

- `KakaoMapSearchApi`
    - [x]  키워드로 장소 검색하기 카카오 API 호출할 때, `page` 값을 1로 고정해서 응답값을 가져오는데 반복문으로 최대 45개까지 가져올 수 있도록 해야하지 않을까요? 현재 상황에서는 `total_count` 값이 15보다 크더라도 15개만 가져오는 것 같습니다.
        
        [로컬 api 키워드로 장소 검색 최대 행 수가 45개 인가요?](https://devtalk.kakao.com/t/api-45/37713/3)
        
    - [x]  포토그레이, 셀픽스 등 지점이 마커에 표시되지 않거나, 검색이 안되는 문제
        - `brand` 의 default 값을 “즉석사진” 으로 하고 `DEFAULT_QUERY_WORD` 삭제는 어떤지
        - 키워드 검색 API 호출할 때만 검색어 뒤에 `사진` 붙여서 검색하고 내주변 API 호출할 때는 쿼리명 그대로 검색하는 것이 어떨지
        
        [GitBook](https://app.gitbook.com/o/emlNjMz5Y4reISMVGPyD/s/Iym42h0SChtj4ZhNp4xk/api-reference/shop-api#undefined-2)
        
        → `DEFAULT_QUERY_WORD` 를 `즉석사진` 대신 `사진`
        

### 다음주 계획(선택)

- member
    - [ ]  `test` : 테스트 코드 작성
- member-title
    - [ ]  s3 회원 칭호 5개(컬러/흑백) 이미지 업로드
    - [ ]  `feat` : 회원 칭호 획득 여부에 따라 컬러/흑백 이미지로 응답하도록 수정
    - [ ]  `test` : 테스트 코드 작성
