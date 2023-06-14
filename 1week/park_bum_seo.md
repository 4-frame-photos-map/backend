## 박범서

### 체크리스트(필수)

- [x]  ErrorResponse
- [x]  Shop TDD (70%)
    - Controller
        - [x]  키워드 검색
        - [x]  상점 상세보기
        - [ ]  주변 상점 마커 표시
    - Service
        - [x]  키워드 검색
        - [x]  상점 상세보기
        - [ ]  주변 상점 마커 표시

### 참고자료(선택)

- TDD
    
    [SpringBoot의 MockMvc를 사용하여 GET, POST 응답 테스트하기](https://shinsunyoung.tistory.com/52)
    
    - Controller 테스트 중, 파라미터가 여러개 일때
        
        ```bash
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("key1", "value2");
        info.add("key1", "value2");
        
        mocvkMvc.perform(~)
        				.params(info);
        ```
        
    - andExpect에서 jsonPath 사용할 때 참고
        
        [MockMvc를 이용한 REST API의 Json Response 검증](https://ykh6242.tistory.com/entry/MockMvc%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-REST-API%EC%9D%98-Json-Response-%EA%B2%80%EC%A6%9D)
        

### 수정사항

- ShopService → findShops (로직변경)

### 궁금한점(선택)

- **ErrorResponse**
    - 기존 포멧
        
        ```json
        {
          "code": {},
          "message": {},
          "result": {
            "data...": {}
          }
        }
        ```
        
    - 성공 응답
    
        ![Untitled](https://user-images.githubusercontent.com/81248569/214575488-71bc2355-c00d-47e1-bd64-7ca06046abf6.png)
        
    - 실패 응답
                
        ![Untitled 1](https://user-images.githubusercontent.com/81248569/214575222-5a84967f-1eec-4d05-a145-243b6ed74fb6.png)
        
    
    ---
    
    - 버전 1 ( `기존포멧` )
        
        ```json
        // 기존 포멧대로 하고, ErrorResponse 일때는 result를 null값 주기
        
        {
          "code": 403,
          "message": "실패",
          "result": null
        }
        ```
        
    - 버전 2 ( `카카오 응답 포멧`)
        
        <img width="843" alt="Untitled 2" src="https://user-images.githubusercontent.com/81248569/214575440-3367eabf-ac1d-418e-a380-2ee182ab531d.png">
        
        - **성공 응답**
            
            ```json
            {
            	"success": true,
            	"result": {
                    ...
                }
            }
            ```
            
        - **실패 응답**
            
            ```json
            {
              "success": false,
              "error": {
                    "errorCode": "404",
                    "errorMessage": "상점을 찾을 수 없습니다."
              }
            }
            ```
            

### 다음주 계획(선택)

- KaKao Map Api 적용 후, 리팩토링
- 키워드 검색
- 마커 검색
