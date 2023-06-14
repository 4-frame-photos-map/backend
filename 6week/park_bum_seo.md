## 이름 

### 체크리스트(필수)

- [ ]  1. NCP 배포 테스트
    - [x]  NCP 셋팅
    - [x]  도커
        - ~~MariaDB~~
            - `Cloud DB for MySQL` 으로 대체할까 고려중
                
                서비스 DB를 안정적으로 운영하고자 하는 경우 적합하기 떄문
                
        - ~~nginx-proxy-manager~~
        - ~~redis~~
    - [x]  임시 배포
        - 배포중 자잘한 오류 및 포트 오류로 인해서, `8090` 포트로 임시 배포 테스트
            - TODO)
                - 포트 변경 : `8090` → `8080`
                - nginx 프록시  : `80`  → `8080`
    - [ ]  CI/CD
- [ ]  2. 찜
    - 스케쥴링 로직으로 변경
- [ ]  3. Shop 마커 검색 “즉석사진”으로 검색

### 에러 해결(선택)

- MariaDB 대소문자 구별 에러
    
    **발생원인**
    
    스프링 부트에서는 DB를 조회할 때, 대문자로 서칭 → SHOP
    
    하지만 MariaDB에 저장된 테이블명은 소문자 → shop
    
    그래서 스프링 부트에서는 테이블이 없다고 오류를 발생시킴
    
    **해결방법**
    
    <aside>
    💡 lower_case_table_names
    
    </aside>
    
    l**ower_case_table_names란**
    
    MySQL이 대소문자를 구분하는 방법
    
    l**ower_case_table_names의  value값 의미** 
    
    | value | 설명 |
    | --- | --- |
    | 0 | 대소문자를 구분합니다. Windows 또는 MacOS와 같이 대소문자를 구분하지 않는 파일 이름을 가진 시스템에서 MySQL을 실행하는 경우 이 변수를 0으로 설정하는 것을 추천하지 않습니다. |
    | 1 | 테이블 이름은 디스크에 소문자로 저장되며 이름 비교는 대소문자를 구분하지 않습니다. MySQL은 저장 및 조회 시 모든 테이블 이름을 소문자로 변환하여 저장합니다. 이 동작은 데이터베이스 이름 및 테이블 별칭에도 동일하게 적용됩니다. |
    | 2 | 테이블과 데이터베이스 이름은 CREATE TABLE 또는 CREATE DATABASE 문에 지정된 문자 케이스를 사용하여 디스크에 저장되지만 MySQL은 조회 시 소문자로 변환한다. 이름 비교는 대소문자를 구분하지 않습니다. 이것은 대소문자를 구분하지 않는 파일 시스템에서만 작동합니다. |
    - lower_case_table_name 디폴값은 0으로 셋팅되어있음
    - 즉, 대소문자를 구분하겠다는 의미이기 때문에 오류를 발생시킴
    
    l**ower_case_table_names = 1로  변경하는 방법 ( `도커` )**
    
    <aside>
    💡 1. 도커 마리아 설치 하고 내부 진입후
    
    docker exec -it [컨테이너명 ex)mariadb] bash (/bin/bash 라고 안하고 bash 라고만 해도 됨)
    
    1 - vi /etc/mysql/my.cnf 
        
        에서 젤 하단에 아래와 같이
        
        [mysqld]lower_case_table_names  = 1
        
    2 - docker restart [컨테이너명 ex)mariadb]
    
    3 - 디비 접속
    
    show variables like 'lower_case_table_names';
    
    요거 날려서 1 이라고 나오면 정상 적용
    
    </aside>
    
- DB 조회 오류
    
    ```bash
    java.sql.SQLNonTransientConnectionException: 
    Socket fail to connect to host:address=(host=localhost)(port=3306)
    (type=primary).
    Connection refused at org.mariadb.jdbc.client.impl.ConnectionHelper.connectSocket(ConnectionHelper.java:136) ~[mariadb-java-client-3.0.9.jar!/:na]
    ```
    
    DB 접속 url, 호스트 아이디, 비밀번호를 전부 확인해봤지만 DB에 접속할 수 없다는 오류가 발생
    
    ```bash
    # application.yml -> datasource:
    url: jdbc:mariadb://**localhost**:3306/four_cut_photos_map?
    useUnicode=true&characterEncoding=utf8&serverTimeZone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false
    ```
    
    **localhost** → `공인IP`로 변경해서 해결
    
    하지만, 왜 localhost가 안되는지는 원인 파악을 못했음.
    

### 에러 사항

- 테스트 코드 전부 실패
    - 아직 원인 파악 못함 ,일단은 주석 처리
- favoriteCnt → Integer → null
    - Integer → int로 일단은 변경 ( 배포 테스트 )
- shop 상세보기 - >shopTitles 데이터 null처리

### 의논 사항

- [x]  Cloud DB for MySQL
- [x]  도메인 구매
- [x]  카카오 로그인 redirect
    - locahlost:8081 → 도메인:8081

### 공유 사항

- 접속 방법
    1. `공인용 IP 접속` 
        
        ip : 49.50.175.194,  포트 : 22
        
        ```bash
        ssh root@49.50.175.194 -p 22 
        ```
        
    2. `서버 접속용 공인 IP로 접속` (포트 포워딩) 
        
        ip : 210.89.191.47 , 포트 : 2222 
        
        ```bash
        ssh root@210.89.191.47 -p 2222 
        ```
        
    
    **차이**
    
    - `공인용 IP 접속`은 네이버 클라우드 플랫폼에서 5분 후에 강제 종료시킴 ( 보안 )
    - `서버 접속용 공인 IP 접속`은 계속해서 사용 가능 ( 권장 )
    

### 다음주 계획(선택)

- `Cloud DB for MySQL`
    - 위 방식으로 DB를 구축한다면, MARIADB → MYSQL로 변경해야됨
      
- 배포 포트 변경 : 8090 → 8080
- nignx 프록시 : 80 → 8080
- ssl
- CI/CD
- 테스트 코드 오류 원인
