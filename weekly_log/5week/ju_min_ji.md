## 주민지

### 체크리스트(필수)
- [x] **refactor** : 위도, 경도 Kakao API에서 받아오기로 수정 -> 불필요해진 위경도 컬럼 및 필드 삭제
   - [x] Shop 테이블, Shop 상세페이지 응답 DTO(ResponseShopDetail)에서 위도, 경도 삭제
   - [x] Shop entity->DTO 변환 메서드에서 위도, 경도 삭제
   - [x] 샘플데이터(data.sql)에서 위도, 경도 컬럼 삭제
   - [x] Shop Test Code 에 반영
- [x] **refactor** : Shop 상세페이지 Controller 사용자의 찜 여부 판단 로직 수정
- [ ] ~~**refactor** : Shop Test Code Mockito로 리팩토링~~

### 참고자료(선택)
#### 1. Shop 상세페이지(Controller)에서 `사용자의 찜 여부 로직`을 따로 모듈화하여 Service Layer로 옮기지 않은 이유
##### Problem
- 스프링 `bean`끼리 순환참조 문제 발생(`FavoriteService `<-> `ShopService`)
- 실제로 참조하지 않고 선언(+주입)만 해놓아도 문제 발생

<img width="700" alt="Pasted Graphic" src="https://user-images.githubusercontent.com/63441091/221593951-2bad725d-eaed-4493-857e-fc3a7df469af.png">

```
Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans. As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
```
##### Solution
[스프링에서 `dependency cycle between beans`가 기본적으로 `false`로 설정되어 있기 때문에 true로 설정 추가하기](https://github.com/springdoc/springdoc-openapi/issues/1347) 
<br> 그러나 스프링 이념을 깨면서까지 서비스 로직으로 반드시 분리해야 하는 코드라고는 생각되지 않아 Controller 코드 내에서 최대한 간략하게 구현

<br>

#### 2.`mockito-test`로 리팩토링하다가 기존 `spring-boot-test`로 회귀한 이유

> **한 줄 요약** : 일일히 행위를 정의해야 해서 테스트케이스 구현에 너무 많은 시간이 소요될 것으로 판단

<br>

 `Mockito`로 테스트하여 return되는 값은 `spring-boot-test`처럼 실제 클래스의 객체가 아니기 때문에 여러 Service, Repository가 중첩되어 있는 경우 이에 해당 Service나 Respository 동작들을 `Mock`(가짜) 객체에 주입하여 간략하게라도 정의가 필요했습니다.
특히 단순 비즈니스 로직이 아닌 외부 API를 호출해야 하는 경우는 더 복잡했습니다. 행위(메서드 실행/호출 여부)만 검증한다면 Mock으로도 커버가 됐지만 결과값에 대한 검증이 필요하다면 Mock으로 커버되지 않고 실행하는 모든 동작들을 정의하는 `Stub`객체까지 구현해야 했습니다. 
<br> 즉 외부 API 통신 포함된 서비스 테스트를 Mockito로 구현해보니 `Service` 단위 테스트가 아닌 `행위` 단위 테스트가 되어버렸습니다.
<br> 이런 문제 때문에 외부 API 서비스 테스트 시 Mockito가 아닌 MockServer을 이용하여 테스트하는 경우들이 많은 것 같았습니다.

> **결론**: 외부 API가 포함된 Service 단위 테스트는 Mockito보다는 SpringBootTest나 MockServer을 이용하는 것이 비용(시간)적인 면에서 효율적
<details>
<summary> Mockito Test 참고자료</summary>
<div markdown="1">

  - [참고자료 - 1](https://jobc.tistory.com/229?category=685104)
  - [참고자료 - 2](https://wave1994.tistory.com/179)
  - [참고자료 - 3](https://tecoble.techcourse.co.kr/post/2020-10-16-is-ok-mockito/)
  - [참고자료 - 4 Stub vs Mock vs Spy](https://luran.me/343)
  - [참고자료 - 5 Stub을 통한 외부 API Test](https://jojoldu.tistory.com/637)
  - [참고자료 - 6 외부 서버와 통신 Test](https://tecoble.techcourse.co.kr/post/2020-09-30-mocking-server/)

</div>
</details>

### 궁금한 점(선택)
개인적으로 궁금했던 점 외에 코드 관련하여 받았던 질문 답변 보충하여 정리해놓았습니다.
- Q. Shop `Entity`에서 favoriteCnt 컬럼 `int`가 아닌 `Integer`로 설정한 이유가 있나요?
  - A. 나중에 MySQL의 `INT`타입을 JAVA의 `int`타입과 헷갈릴 것을 대비하여 **명확하게 Integer로 설정**했습니다. 
    - 표준 SQL에서 정의된 것은 Integer이라는 키워드 뿐이고, MySQL에서 INT는 INTEGER를 가리키는 또 다른 명칭일 뿐 둘은 결국 같은 속성이었습니다.
    - 테스트 결과 **int**로 선언하여 데이터를 INSERT해도 디폴트값은 0이 아닌 **null**로 추가되었습니다
    - 만일 디폴트값을 null이 아닌 0으로 설정하고 싶다면 DB 테이블 설정에서 디폴트값을 변경해주어야 했습니다.
    - [INTEGER, INT 참고자료](https://spiderwebcoding.tistory.com/5)

### 다음주 계획(선택)

- **feat** : Shop 초기데이터 생성
  - CSV -> DB 변환 자료조사
- **refactor** : Shop 키워드 검색에 현재 위치 거리순 정렬 기능 추가
  - distance 필드 추가
  - 거리 포맷팅 코드 적용
