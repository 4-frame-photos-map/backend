package com.idea5.four_cut_photos_map.domain.shoptitlelog.service;

import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.service.ShopTitleService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitleType.HOT_PLACE;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ShopTitleLogServiceTest {

    @Autowired
    private ShopTitleLogService shopTitleLogService;

    @Autowired
    private ShopTitleService shopTitleService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopTitleRepository shopTitleRepository;

    @Autowired
    private ShopTitleLogRepository shopTitleLogRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteService favoriteService;

    @DisplayName("ShopTitleLog 조회, 성공한 경우")
    @Test
    void findShopTitleLogs_success() {
        // given
        Shop shop = new Shop( "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58", 0);
        shopRepository.save(shop);

        ShopTitle shopTitle1 = new ShopTitle("핫 플레이스", "찜 수 5개 이상", "사람들이 주로 이용하는 포토부스에요.");
        ShopTitle shopTitle2 = new ShopTitle("청결 양호", "청결 점수 4점 이상", "시설이 깔끔해요.'");
        ShopTitle shopTitle3 = new ShopTitle("보정 양호", "보정 점수 4점 이상", "막 찍어도 잘 나와요.");
        ShopTitle shopTitle4 = new ShopTitle("소품 양호", "소품 점수 4점 이상", "다양하게 연출하기 좋아요.");
        shopTitleRepository.save(shopTitle1);
        shopTitleRepository.save(shopTitle2);
        shopTitleRepository.save(shopTitle3);
        shopTitleRepository.save(shopTitle4);

        shopTitleLogRepository.save(new ShopTitleLog(shop, shopTitle1));
        shopTitleLogRepository.save(new ShopTitleLog(shop, shopTitle2));
        shopTitleLogRepository.save(new ShopTitleLog(shop, shopTitle3));
        shopTitleLogRepository.save(new ShopTitleLog(shop, shopTitle4));


        // when
        List<ShopTitleDto> shopTitleDtos = shopTitleLogService.findShopTitlesByShopId(shop.getId());
        // then

        assertThat(shopTitleDtos.size()).isEqualTo(4);
    }

    @DisplayName("ShopTitleLog 조회 시, 실패한 경우 예외를 발생시킨다. ")
    @Test
    void findShopTitleLogs_fail() {
        // given
        Shop shop = new Shop( "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58",0);
        shopRepository.save(shop);

        ShopTitle shopTitle1 = new ShopTitle("핫 플레이스", "찜 수 5개 이상", "사람들이 주로 이용하는 포토부스에요.");
        ShopTitle shopTitle2 = new ShopTitle("청결 양호", "청결 점수 4점 이상", "시설이 깔끔해요.'");
        ShopTitle shopTitle3 = new ShopTitle("보정 양호", "보정 점수 4점 이상", "막 찍어도 잘 나와요.");
        ShopTitle shopTitle4 = new ShopTitle("소품 양호", "소품 점수 4점 이상", "다양하게 연출하기 좋아요.");
        shopTitleRepository.save(shopTitle1);
        shopTitleRepository.save(shopTitle2);
        shopTitleRepository.save(shopTitle3);
        shopTitleRepository.save(shopTitle4);


        // when, then
        assertThrows(BusinessException.class, () -> {
            List<ShopTitleDto> shopTitleList = shopTitleLogService.findShopTitlesByShopId(shop.getId());
            assertThat(shopTitleList.size()).isEqualTo(0);
        });

    }

    @DisplayName("ShopTitleLog 추가")
    @Test
    void addShopTitleLog() {
        // given
        Shop shop = new Shop( "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58", 0);
        shopRepository.save(shop);

        ShopTitle shopTitle1 = new ShopTitle("핫 플레이스", "찜 수 5개 이상", "사람들이 주로 이용하는 포토부스에요.");
        ShopTitle shopTitle2 = new ShopTitle("청결 양호", "청결 점수 4점 이상", "시설이 깔끔해요.'");
        ShopTitle shopTitle3 = new ShopTitle("보정 양호", "보정 점수 4점 이상", "막 찍어도 잘 나와요.");
        ShopTitle shopTitle4 = new ShopTitle("소품 양호", "소품 점수 4점 이상", "다양하게 연출하기 좋아요.");
        shopTitleRepository.save(shopTitle1);
        shopTitleRepository.save(shopTitle2);
        shopTitleRepository.save(shopTitle3);
        shopTitleRepository.save(shopTitle4);

        // when
        shopTitleLogService.save(shop.getId(), shopTitle1.getId());
        shopTitleLogService.save(shop.getId(), shopTitle2.getId());
        shopTitleLogService.save(shop.getId(), shopTitle3.getId());
        shopTitleLogService.save(shop.getId(), shopTitle4.getId());


        // then
        List<ShopTitleDto> list = shopTitleLogService.findShopTitlesByShopId(shop.getId());
        assertThat(list.size()).isEqualTo(4);


    }
    // assertJ 참고 사이트 : https://umanking.github.io/2021/06/26/assertj-iteration/
    @DisplayName("상점이 보유한 타이틀 제거")
    @Test
    void deleteShopTitle() {
        // given
        Shop shop = new Shop( "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58", 0);
        shopRepository.save(shop);

        ShopTitle shopTitle1 = new ShopTitle("핫 플레이스", "찜 수 5개 이상", "사람들이 주로 이용하는 포토부스에요.");
        ShopTitle shopTitle2 = new ShopTitle("청결 양호", "청결 점수 4점 이상", "시설이 깔끔해요.'");
        ShopTitle shopTitle3 = new ShopTitle("보정 양호", "보정 점수 4점 이상", "막 찍어도 잘 나와요.");
        ShopTitle shopTitle4 = new ShopTitle("소품 양호", "소품 점수 4점 이상", "다양하게 연출하기 좋아요.");
        shopTitleRepository.save(shopTitle1);
        shopTitleRepository.save(shopTitle2);
        shopTitleRepository.save(shopTitle3);
        shopTitleRepository.save(shopTitle4);

        //  저장 후, 삭제될 칭호
        ShopTitleLog removeShopTileLog = new ShopTitleLog(shop, shopTitle1);
        // 영구 저장될 칭호
        ShopTitleLog shopTitleLog2 = new ShopTitleLog(shop, shopTitle2);
        ShopTitleLog shopTitleLog3 = new ShopTitleLog(shop, shopTitle3);
        ShopTitleLog shopTitleLog4 = new ShopTitleLog(shop, shopTitle4);

        shopTitleLogRepository.save(removeShopTileLog);
        shopTitleLogRepository.save(shopTitleLog2);
        shopTitleLogRepository.save(shopTitleLog3);
        shopTitleLogRepository.save(shopTitleLog4);

        // when
        shopTitleLogRepository.deleteById(removeShopTileLog.getId()); // 삭제

        // then
        List<ShopTitleDto> list = shopTitleLogService.findShopTitlesByShopId(shop.getId());
        assertAll(

                // 상점에 부여된 칭호는 4개 였지만, 칭호 하나가 삭제 되어 3이 출력
                () -> assertThat(list.size()).isEqualTo(3),

                // list 조회시, 삭제된 칭호는 조회될 수 없다. ( doesNotContain )
                () -> assertThat(list).extracting("name", "conditions", "content")
                      .doesNotContain(tuple(shopTitle1.getName(), shopTitle1.getConditions(), shopTitle1.getContent())),

                // list 조회시, 기존에 있던 칭호들은 모두 조회된다. ( contains )
                () -> assertThat(list).extracting("name", "conditions", "content")
                        .contains(
                                tuple(shopTitle2.getName(), shopTitle2.getConditions(), shopTitle2.getContent()),
                                tuple(shopTitle3.getName(), shopTitle3.getConditions(), shopTitle3.getContent()),
                                tuple(shopTitle4.getName(), shopTitle4.getConditions(), shopTitle4.getContent())
                                )

        );

    }

    @DisplayName("좋아요 수가 5개 이상이면 핫플레이 칭호를 부여한다.")
    @Test
    @Rollback(value = false)
    void testHotPlaceTitle() {
        // given

        // 핫 플레이스 칭호 DB저장
        ShopTitle shopTitle = new ShopTitle(HOT_PLACE.getName(), HOT_PLACE.getConditions(), HOT_PLACE.getContent());
        shopTitleRepository.save(shopTitle);

        // 테스트용 멤버 5명 저장
        Member memberA = new Member();
        Member memberB = new Member();
        Member memberC = new Member();
        Member memberD = new Member();
        Member memberE = new Member();
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);
        memberRepository.save(memberE);

        // 좋아요 대상 Shop 저장
        Shop shop = new Shop( "인생네컷 천안안서점", "충남 천안시 동남구 상명대길 58", 0);
        shopRepository.save(shop);

        // when

        favoriteService.save(shop.getId(), memberA);
        favoriteService.save(shop.getId(), memberB);
        favoriteService.save(shop.getId(), memberC);
        favoriteService.save(shop.getId(), memberD);
        favoriteService.save(shop.getId(), memberE);

        // then

        // 좋아요 수가 5개 이상인지 체크
        boolean isHotPlace = favoriteService.isHotPlace(shop.getId()); // todo : FavoriteServiceTest에서 수행해야 됨
        ShopTitleLog shopTitleLog = shopTitleLogService.findShopTitleLog(shop.getId(), HOT_PLACE.getId());

        assertAll(
                () -> assertThat(isHotPlace).isTrue(), // 핫플레이스 조건 부합 체크
                () -> assertThat(shopTitleLog).isNotNull(), // 해당 ShopTitle DB 저장 유무 체크
                () -> assertThat(shopTitleLog.getShopTitleName()).isEqualTo(HOT_PLACE.getName()) // 해당 ShopTitle이 핫 플레이스인지 체크
        );
    }

}