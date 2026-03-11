package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.repository.MemberRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.UnexpectedRollbackException;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@ActiveProfiles("test")
class FavoriteServiceIntegrationTest {

    @SpyBean
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;


@Test//  준비 환경: Favorite에 낙관적 락 version 추가
@DisplayName("UPDATE 상황에서 조회가 플러시를 유발하여 내부 try-catch가 예외를 잡는지 확인")
void verifyUpdateFlushTest() {
    // Given
    Object[] ids = transactionTemplate.execute(status -> {
        Member member = memberRepository.save(new Member());
        Brand brand = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));

        Shop shop1 = shopRepository.save(new Shop(brand, "성수점", "주소1", 0, 0, 0.0));
        Shop shop2 = shopRepository.save(new Shop(brand, "강남점", "주소2", 0, 0, 0.0));

        favoriteService.save(shop1.getId(), member);
        Favorite favorite = favoriteService.findByShopIdAndMemberId(shop1.getId(), member.getId());

        return new Object[]{favorite.getId(), member.getId(), shop1.getId()};
    });

    Long favoriteId = (Long) ids[0];
    Long memberId = (Long) ids[1];
    Long shopId = (Long) ids[2];

    // When & Then: 낙관적 락 시나리오 재현
    transactionTemplate.execute(status -> {
        // [A] 미끼 던지기: 서비스 트랜잭션의 1차 캐시에 '버전 0'인 엔티티 로드
        Favorite favorite = favoriteService.findByShopIdAndMemberId(shopId, memberId);

        // [B] 가로채기: 같은 트랜잭션 내에서 Native Query로 DB 버전만 1로 올림
        // 영속성 컨텍스트(1차 캐시) 속의 favorite 객체는 여전히 버전이 0인 상태
        em.createNativeQuery("UPDATE favorite SET version = version + 1 WHERE id = ?")
                .setParameter(1, favoriteId)
                .executeUpdate();

        // [C] 충돌 유발: 이제 서비스의 updateTest를 호출
        // 내부에서 favorite을 다시 조회해도 1차 캐시 덕분에 '버전 0'인 객체를 사용하게 됨
        // 그 후 수정을 가하고 findAll()을 호출하는 순간!
        // 하이버네이트가 "버전 0 -> 2"로 업데이트를 시도하다가 DB의 "버전 1"과 충돌하여 예외를 던짐.
        favoriteService.updateTest(shopId, memberId);

        return null;
    });
}
    @Test
    @DisplayName("cancel 메서드의 try-catch가 지연 쓰기(Write-Behind)로 인해 예외를 잡지 못함을 확인")
    void verifyCancelTryCatchFailureDueToDelayedFlush() {
        // Given
        final Long[] ids = transactionTemplate.execute(status -> {
            Member member = memberRepository.save(new Member());
            Brand brand = brandRepository.save(new Brand(MajorBrand.LIFEFOURCUTS.getBrandName(), MajorBrand.LIFEFOURCUTS.getFilePath()));
            Shop shop = shopRepository.save(new Shop(brand, "성수점", "주소1", 0, 0, 0.0));

            Favorite favorite = Favorite.builder().member(member).shop(shop).build();
            favoriteRepository.save(favorite);
            return new Long[]{favorite.getId(), member.getId(), shop.getId()};
        });

        Long favoriteId = ids[0];
        Long memberId = ids[1];
        Long shopId = ids[2];

        // When & Then
        assertThrows(UnexpectedRollbackException.class, () -> {
            transactionTemplate.execute(status -> {
                doAnswer(invocation -> {
                    Object result = invocation.callRealMethod();

                    // 조회가 성공하자마자 DB에서 레코드를 삭제 (레이스 컨디션 재현)
                    em.createNativeQuery("DELETE FROM favorite WHERE id = ?")
                            .setParameter(1, favoriteId)
                            .executeUpdate();

                    return result;
                }).when(favoriteService).findByShopIdAndMemberId(anyLong(), anyLong());

                favoriteService.cancel(shopId, memberId);

                // 해당 로그가 찍히면 쓰기 지연으로 인해 내부 try-catch가 무력화되었음이 증명
                System.out.println("--- 서비스 cancel() 종료 (가설대로 try-catch 통과됨) ---");

                return null;
            });
        });
    }
}