## 주민지
### 체크리스트(필수) 
-  **refactor** : 데이터 정합성
    - [x] 리뷰 개수, 리뷰 평점, 찜 수 특정 주기로 더블 체크
- **refactor** : 캐시로 성능 개선 
   - [x] 외부 API 자주 호출하는 코드 캐싱
      - 상세조회 API에만 적용 
- **refactor** : Spring Web Client 변경
   - [x] Rest RestTemplate을 Web Client로 변경
      - 현재는 기존과 동일하게 동기 호출 (카카오 API에서 사용하는 sort 기능을 유지하기 위해)
- **refactor** : 조회 정확도 개선 
     - 일반스튜디오가 아닌 즉석사진으로 모든 응답 통일
       - [x] 상세조회 API, 찜목록조회API에서 호출한 카카오 API에서 상위 1개의 응답만 반환하지 않고 모든 응답 반환 후 도로명 주소와 100% 일치하는지 검사하도록 수정하여 정확도 개선
           - `도로명주소+DEFAULT_QUERY_WORD`로 조회 시 100% 일치 결과가 최상단에 노출되지 않는 경우도 존재 
   - [x] 마커, 리스트 API에서 카카오 API 호출 시 즉석사진 카테고리로 한번 더 필터링
      - 유사도 높은 검색결과도 포함되어 일반 사진 스튜디오도 반환하는 경우 존재
- **refactor** : DB 성능 개선
   - [ ] DB에서 사용하지 않는 일반스튜디오 데이터 분리하여 삭제 작업할 수 있도록 환경 구성하여 Shop 테이블 성능 개선
       - 수정 날짜로 분할
- **refactor** : URI, Parameter, Response 수정
  - [x] 키워드조회, 브랜드별조회 응답 DTO 분리하여 키워드조회에만 도로명주소 응답 추가
  - [x] 간단조회 API에서 불필요한 파라미터 제거
  - [x] 브랜드별 조회 API에서 brand 파라미터 Optional 처리
  - [x] 도로명주소 필요없는 API 응답에서 제거
  - [x] Controller 필수 파라미터 누락 예외(`MissingServletRequestParameterException`) 처리 커스텀 
      - 500 에러-> 400 에러
      - 에러 메세지 `[에러필드] 에러메세지`로 구조화
  - [x] 찜 개수 초과 시 에러 메세지에 초과 기준치 포함하도록 수정

### 참고 사항(선택)
#### I.상세조회 기능에서만 외부 API 응답값 캐시한 이유      
> 불변 데이터만 캐시하기 위하여
- 상세 조회 기능은 카카오 API에서 불변 데이터만 받아옴 (distance는 파라미터로 받아오므로)
- 이와 달리 카카오 API를 호출하는 다른 API(키워드 조회, 브랜드별 조회, 찜 목록)는 위도, 경도에 따라 변하는 값(`distance`)이 존재하기 때문에 매번 호출할 수밖에 없음
#### II. Kakao Map API 호출 클래스 기존과 동일하게 동기 호출로 구현했지만, Web Client로 변경한 이유
1. 데이터 순차 정렬 기능이 필요없어진다면 스레드를 비동기로 처리하여 병렬 작업하거나, 비동기로 외부 API 응답 받은 후에 정렬하는 방식으로도 수정하거나 확장할 수 있기 때문에 비동기 호출이 가능한 Web Client로 수정
2. Spring Framework 5.0부터 RestTemplate은 deprecated -> RestTemplate은 Spring Framework 5.3까지는 사용 가능하지만, 더 이상 활발히 유지보수되지 않기 때문에 Web Cleint로 수정
- Reference
   - [Web Client Spring 공식문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client-synchronous)
   - [Reactor Scheduler 공식문서 - 비동기 프로그래밍 시나리오에 유연하게 대응하기 위한 라이브러리](https://projectreactor.io/docs/core/release/reference/#schedulers)
   - [Web Client에서 JSON 필드 얻는 방법](https://stackoverflow.com/questions/54659473/how-to-get-a-json-field-by-name-using-spring-webclient)
   - [Flux 3 - 스레드와 스케줄러 알아보기](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=sthwin&logNo=221956619428)
   - [[SpringBoot] SpringMVC 에서 WebClient 사용시 주의사항](https://findmypiece.tistory.com/276)
   - [Web Client 적응기](https://leeggmin.tistory.com/11)
   - [Web Client 동작 원리](https://velog.io/@ljo_0920/Spring-WebClient)
#### III. Redis Cache 확인
- redis-cli로 redis 내부 접속
     <img width="705" alt="Pasted Graphic" src="https://user-images.githubusercontent.com/63441091/231373626-a4ad7fe1-010d-425f-b41d-d83e7910d20f.png">
 - Cache Hit 와 Miss 비율 확인하기 위해 Hit, Miss 시 각 각 로그에 남도록 처리
   <img width="1313" alt="Pasted Graphic 1" src="https://user-images.githubusercontent.com/63441091/231373939-72164666-0924-4023-9d17-63207e2f5bff.png">
### IV. DB에서 브랜드명이나 지역명 분할하지 않기로 결정한 이유
- 파티션 기준이 아닌 다른 기준으로 조회 시 파티션 간 병합 작업으로 인해 오버헤드 발생할 수 있기 때문
- 따라서, 브랜드명이나(인생네컷, 포토이즘박스, 하루필름, 포토그레이, 그 외) 지역명 별로 분할 대신 전체 테이블에 `브랜드` 인덱스와 `도로명주소` 인덱스만 설정하기로 결정
- Reference
  - [파티셔닝](https://velog.io/@gillog/MySQL-Partition#partition%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0)
### 논의 사항(선택)
- 유니크한 에러코드 공통 적용
    - ModelAttribute를 사용하여 요청 파라미터를 관리하면 파라미터 누락 시 파라미터 누락 Exception이 아닌 BindException으로 처리되어 항상 타입 에러코드 반환
       - ModelAttribute를 통해 HTTP 파라미터도 바인딩 가능하나, 폼 데이터 바인딩이 주 사용 용도이기 때문에 파라미터 누락 Exception이 나지 않는 걸로 추측
    ```
    org.springframework.validation.BindException: org.springframework.validation.BeanPropertyBindingResult: 1 errors
    Field error in object 'requestShopBriefInfo' on field 'placeName': rejected value []; codes [NotBlank.requestShopBriefInfo.placeName,NotBlank.placeName,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [requestShopBriefInfo.placeName,placeName]; arguments []; 
    default message [placeName]]; default message [지점명은 필수 입력값 입니다.]
    ```
    → 공통 적용 시 @ModelAttribute 사용하여 DTO로 관리하고 있는 파라미터들은 전부 @RequestParam 붙여서 개별 파라미터로 수정 필요
### 다음주 계획(선택)
- Shop 파라미터 @RequestParam으로 전부 수정
- DB Shop 테이블 브랜드 컬럼 인덱스 설정, 즉석사진관이 아닌 브랜드 row 삭제
- DB 초기데이터(경기) 추가 작업
- Shop Test 리팩토링
- ShopTitle 부여 기준(청결도) 추가, 스케줄링 작업
