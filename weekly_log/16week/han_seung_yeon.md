## 한승연

### 체크리스트(필수)

- member-title
    - [x]  `fix` : 칭호 전체 조회 로직 문제 해결
    - [x]  `fix` : 회원의 대표 칭호가 2개 이상일 때 대표 칭호 설정 요청에 실패하는 오류 해결
    - [x]  `feat` : MemberTitle 설명 필드 추가 및 ERD 수정
    - [x]  `feat` : 칭호 전체 조회 API 응답에 획득방법 및 설명 추가
    - [x]  `fix` : 회원 칭호 스케줄링 매일 오전 2시로 변경
- review
    - [x]  `fix` : 지점 전체 리뷰 조회 API 응답의 `member_info` 에 회원 대표 칭호 데이터 추가
- shop
    - [x]  `fix` : 지점 상세 조회 API 응답의 `member_info` 에 회원 대표 칭호 데이터 추가
- infra
    - [x]  RDS 과금 문제로 인해 새로운 계정의 RDS로 데이터 이관 작업

### 참고자료(선택)

### 궁금한점(선택)

- [x]  카카오 API 응답 데이터과 DB 데이터의 주소와 지점명이 같을 때만 검색되기때문에 같은 지점임에도 불구하고 지점명이 달라서 응답에 포함되지 않는 한계가 존재함 → 동일 주소에 같은 브랜드 지점이 존재하는 경우가 있어 브랜드명으로 검사하는 것은 안됨
    - 지점명이 아닌 브랜드명으로 일치하는지 검사
    1. `kakaoPlaceName` 에서 `brandName` 추출하기
        
        ```java
        public getBrandName(String placeName) {
        		String[] brands = {"인생네컷", "하루필름",,,};
        		for(String brand : brands) {
        				if(kakaoPlaceName.startsWith(brand)) {
        						return brand;
        				}
        		}
        		return null;
        }
        ```
        
    2. `dbPlaceName` 문자열이 `brandName` 으로 시작하는지 검사
        
        ```java
        if(dbPlaceName.startsWith(brandName)) {
        		return dbData;
        }
        ```
        
    
    | DB 데이터 | 카카오 API 응답 데이터 |
    | --- | --- |
    | 포토드링크 충장로점 | 포토드링크 광주충장로점 |
    | 포토그레이 광주 동명 RAD점 | 포토그레이 동명점 |

### 다음주 계획(선택)

- member-title
    - [ ]  `test` : 칭호 전체 조회 테스트 코드 작성
    - [ ]  `test` : 대표칭호 설정 성공/실패 테스트 코드 작성
- member
    - [ ]  `test` : 테스트 코드 작성
