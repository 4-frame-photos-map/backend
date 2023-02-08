## 주민지

### 체크리스트(필수)

- [x] Shop 찜 추가
  - [x] 찜 중복 처리 에러 코드(`DUPLICATE_FAVORITE`) 추가
- [x] Shop 찜 취소
  - [x] 찜 취소 중복 처리 에러 코드(`DELETED_FAVORITE`) 추가
- [x] Shop 상세페이지에 사용자의 찜 가능 여부, 찜 개수도 리턴
   - [x] ResponseShopDetail에 `canBeAddedToFavorites`, `favoriteCnt` 필드 추가
   - **찜 개수 필드 추가 이유** : 자주 사용할 것 같은 찜 개수 필드만 추가 후, Shop, Member Entity와 양방향 매핑관계 끊고 단방향으로 수정
   - **사용자의 찜 가능 여부 필드 추가 이유** : 찜 추가/취소를 각각 PostMapping, DeleteMapping으로 사용하려면 프론트에서 찜 추가 가능여부가 필요하다고 판단(가능여부에 따라 다른 HTTP Method 사용)
### 참고자료(선택)
```java
class Parent {
    @OneToMany(cascade = CascadeType.???, fetch = FetchType.???, mappedBy = "parent")
    List<Child> children = new ArrayList<>();
}

class Child {
   @ManyToOne
   @JoinColumn(name = "lazy_parent_id")
   Parent parent;
}
```
### 궁금한점(선택)
개인적으로 궁금했던 점 외에 코드 관련하여 받았던 질문들도 기록해놓았습니다.
- Q1. 지금은 양방향 매핑 단절로 인해 삭제되었지만, Member,Shop 테이블과 ManyToOne 관계에 있는 Favorite 테이블에 CascadeType.REMOVE 옵션을 줬던 이유는 무엇인가요?
  - A1.Favorite(자식) 테이블은 기본키인 id를 제외하고는 별 다른 컬럼 없이 외래키인 Member(부모)와 Shop(부모)으로 이루어진 테이블입니다. 따라서 찜을 추가한 회원(Member)이나 찜이 된 상점(Shop)이 삭제될 시 그에 연관된 Favorite(자식) 데이터는 의미가 없다고 생각했었습니다. 그러나 부모-자식 간의 연관성이 있다 할 지라도, DB데이터 추적을 위해 Cascade remove 옵션은 팀원들과 상의하여 조금 더 신중히 사용해야 겠습니다. 
```java
// Favorite.java

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Favorite extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

}
```
```java
// 양방향 매핑 단절시키기 전 Member.java
 ..
 
 @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Favorite> favorites = new ArrayList<>();
```
```java
// 양방향 매핑 단절시키기 전 Shop.java
 ..
 
 @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Favorite> favorites = new ArrayList<>();
```
- Q2. 찜한 본인 회원 정보를 응답에 포함한 이유는 무엇인가요?
  - A2. 처음 찜리스트 응답에 본인 회원 정보를 포함한 이유는 '회원 닉네임(or ID)님의 찜 목록 페이지'처럼 페이지 정의(title)에 사용될 수 있다고 생각했기 때문입니다. 
  그런데 회의에서 나왔던 것처럼 메뉴바가 도입될 수도 있는 가능성(매 페이지마다 메뉴바에 필요한 회원 DTO 함께 넣어서 요청)을 고려하여 삭제하는 쪽으로 리팩토링하는 것이 좋을 것 같습니다.
- Q3. 단방향 매핑과 양방향 매핑의 이점과 사용케이스에는 무엇이 있을까요? 
  - A3. 김영한님의 JPA 강의를 참고해보자면, **기본 설계 시 단방향 매핑으로 하되 필요 시에만 양방향 매핑을 추가**(`OneToMany`에 Collections 추가)하는 것을 권장하고 있습니다. 양방향은 단방향이 2개 생성되는 것이나 마찬가지이기 때문입니다. 따라서 테이블을 역조회를 하는 경우가 많다면 양방향 매핑을 그 때 추가하는 것이 양방향 매핑을 의도에 맞게 사용했다고 볼 수 있을 것 같습니다. 
  <br> `Member.favoriteList`가 사용될 수 있는 케이스에는 1)`Favorite` 도메인에서 `회원의 찜 리스트 조회`, 2)`MemberTitle` 도메인에서 `찜 개수에 따른 칭호 부여`가 있을 것 같습니다.
<br> MemberTitle 기능을 맡은 팀원과 회의 결과, 단방향보다 양방향 매핑 구현이 더 복잡하다고 느껴 일단은 `Favorite`과 `Member`는 단방향 매핑을 유지할 것 같습니다. 그러나 `Member.favoriteList` 사용 사례가 많아진다면 언제든 양방향 매핑으로 바꿀 가능성은 열어놓았습니다.
### 다음주 계획(선택)         
- 코드리뷰, 회의에 기반하여 Shop SearchByKeyword 리팩토링
- Shop SearchByKeyword, Favorite Test Code 작성
