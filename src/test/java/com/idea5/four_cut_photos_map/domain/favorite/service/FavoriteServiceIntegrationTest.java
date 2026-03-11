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