## 주민지

### 체크리스트(필수)
- `refactor` : Global
  - [x] Shop, Favorite 성공 응답구조 통일
    - 성공 후 반환데이터 없을 시 빈 배열이나 문자열 응답
    - Get 요청은 실질적으로 사용하는 결과값만 바디에 담아 응답
    - Put, Delete 요청은 응답 바디 생략 (성공 시 디폴트 상태코드 200)
  - [x] **공통 에러 응답(ErrorResponse.class) JsonNaming SnakeCase 반영**
  - [x] **@RequestParam, @PathVariable을 Validation으로 유효성 검사 시 발생하는 Error Handler 추가**
    - ConstraintViolationException 500(Server Error)에서 400(Client Error)로
    - `[error field] msg`로 에러메세지 구조화 (이전에는 Validation BindingResult 에러만)
- `refactor` : Shop 도메인
  - [x]  리뷰 평점, 리뷰 개수 계산하여 update하는 코드 추가
  - [x]  장소 url, 찜 가능 여부, 리뷰 평점, 리뷰 개수, 모든 Shop API 응답에 추가
  - [x] 찜 수 간단 조회, 상세 조회 API 응답에 추가
  - [x]  리스트와 마커형 페이지에서 불필요한 도로명주소 응답에서 제거
- `refactor` : 브랜드별 조회
  - [x]  브랜드 검색(기존 /brand)과 전체 검색(기존 /marker) 로직 합쳐서 중복 제거
- `refactor` : 상세 조회
  - [x]  DB 장소명 대신 카카오 API 장소명 반영하기
  - [x]  지점 상세 조회 페이지 상단에 Map Marker를 그리기 위한 위도, 경도 카카오 API에서 받아오기
  - [x]  @ReqeustParam distance Validation으로 유효성 검사
  - [x] DB 데이터 중 셀프 즉석사진관 업종이 아닌 shop-id가 URI에 포함되었을 때 예외 처리
- `feat` : 간단 조회 API (Map Marker 모달용) 개발 완료
  - [x]  상세조회와 공통 메서드 추출
    - 상세조회 로직과 비슷하여 메서드 공유하기 위해 상세조회 응답 DTO가 간단조회 응답 DTO로부터 상속 받음
- `refactor` : 찜 목록 조회
  - [x] DB 장소명 대신 카카오 API 장소명 반영하기
  - [x] 장소 url, 찜 가능 여부, 리뷰 평점, 리뷰 개수, 중심좌표로부터의 거리 응답에 추가
  - [x] DB 데이터 중 셀프 즉석사진관 업종이 아닌 shop-id가 URI에 포함되었을 때 예외 처리
    - 찜 추가 시에도 유효한 shop-id인지 검사하려면 카카오 API 호출하여 검사 필요(비효율적)
     - **이 문제는 실 서비스가 아닌 API 테스트 시에만 발생하는 문제이기 때문에 찜 추가 후 찜 목록 조회 시 유효하지 않은 `shop-id`는 자동 찜 취소처리**
- `refactor` : 찜 추가
  - [x]  회원 별 찜 수 20개로 개수 제한

### 참고사항(선택)

#### I. Jackson Property Naming Strategy 스프링 전체 설정에 작동하지 않는 이슈

#### 결론

> 스프링 전체 설정 대신 필요한 클래스나 필드에 개별 설정 필요
> 

#### 상황

- 프로젝트에 적용된 jackson-databind 버전 : `com.fasterxml.jackson.core:jackson-databind:2.12`
- [jackson-databind 2.12 공식문서](https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/2.12.2/com/fasterxml/jackson/databind/class-use/PropertyNamingStrategy.html) : PropertyNamingStrategy 비권장(Deprecation)
- Deprecation으로 인해 완전히 지원이 중단되거나 종료된 것은 아니나 일부 기능이 작동하지 않는다는 자료들 존재
- 클래스 단위로 적용되는 @Naming은 PropertyNamingStrategy 대신 PropertyNamingStrategies로 작동
- `yml`이나 `ObjectMapper`에도 설정 시도했으나 미작동

#### 시도 방법 1. yml

- yml의 property-naming-strategy를 클릭 시 `setPropertyNamingStrategy()`로 이동
- `setPropertyNamingStrategy()`가 PropertyNamingStrategies 클래스에 속한 메서드이므로 작동을 기대했지만 미작동
    <img width="629" alt="image" src="https://user-images.githubusercontent.com/63441091/227156792-c003ac83-093b-4d35-a033-d97624c34247.png">

```
# Not Working
spring:
  jackson:
     property-naming-strategy: SNAKE_CASE

```

```
# Not Working
spring:
  jackson:
     property-naming-strategy: SnakeCaseStrategy

```

```
# Not Working
spring:
  jackson:
     property-naming-strategy: 'com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy'

```

#### 시도 방법 2. ObjectMapper에 적용 시도

```
// Not Working
// JacksonConfig.java

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                    ..
                  .setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
   }

```

<details>
<summary>PropertyNamingStrategy 관련 참고자료</summary>
<div markdown="1">

- [참고자료 1 - StackOverFlow](https://stackoverflow.com/questions/74714449/alternatives-for-propertynamingstrategy-snake-case-or-propertynamingstrategy-sna)
    - ObjectMapper에 setPropertyNamingStrategy() 미작동
- [참고자료 2](https://www.appsloveworld.com/springboot/100/180/springboot-restcontroller-not-recognizing-spring-jackson-property-naming-strategy)
    - @Naming은 PropertyNamingStrategy 대신 PropertyNamingStrategies로 작동
- [참고자료 3](https://steady-coding.tistory.com/454)
    - 2021.06 기준 yml에 적용됐던 spring.jackson.property-naming-strategy
- [참고자료 4](http://daplus.net/java-%EB%82%99%ED%83%80-%EC%82%AC%EA%B1%B4%EC%97%90-%EC%B0%AC%EC%84%B1%ED%95%98%EC%97%AC-%EB%B0%91%EC%A4%84%EC%9D%84-%EA%B7%B9%EB%B3%B5%ED%95%98%EB%8A%94-%EC%9E%AD%EC%8A%A8/)
    - yml, ObjectMapper, @Naming, @JsonProperty
- [참고자료 5](https://www.tabnine.com/code/java/methods/com.fasterxml.jackson.databind.ObjectMapper/setPropertyNamingStrategy)

</div>
</details>

#### II. 찜 목록 페이지에서 요청 파라미터 중 criteria는 `@ModelAtttribute` 이용하여 DTO로 관리하지 않은 이유

1. 특정 default value 세팅 옵션은 `@RequestParam`만 가능하므로
    - `@ModelAtttribute`는 따로 지원하지 않기 때문에 생성자에서 원하는 초기값 따로 세팅 필요
2. 쿼리 파라미터명과 속성값을 분리하기 위해 (`sort=criteria`)
    - `@ModelAtttribute`는 모델명과 매개변수명이 일치할 경우 바인딩
- 참고자료
    - [Spring MVC와 @ModelAttribute 어노테이션](https://www.baeldung.com/spring-mvc-and-the-modelattribute-annotation)
    - [@ModelAttribute에서 default value](https://www.inflearn.com/questions/249849/modelattribute%EC%97%90%EC%84%9C-default-value)

#### III. 찜 개수 초과시 상태코드

[Kakao Developers REST API 공식문서](https://developers.kakao.com/docs/latest/ko/reference/rest-api-reference)와 상태코드 관련 자료 검색 결과
API 요청 횟수 초과 시 `429`, 리소스 개수 초과 시 `400` HTTP Status Code를 반환하는 것으로 확인했습니다.

따라서 찜 개수 초과 시에도 `400` 에러를 응답하도록 구현하였습니다.

### 다음주 계획(선택)

- [ ]  리뷰 개수, 리뷰 평점, 찜 수 특정 주기로 데이터 무결성 위하여 더블 체크
- [ ]  외부 API 자주 호출하는 코드 캐싱
- [ ]  데이터베이스 파티셔닝
    - 장소명에 포함된 브랜드명이나(인생네컷, 포토이즘박스, 하루필름, 포토그레이, 그 외) 지역명으로 분할
- [ ]  REST API 클라이언트 RestTemplate에서 Web Client로 변경
