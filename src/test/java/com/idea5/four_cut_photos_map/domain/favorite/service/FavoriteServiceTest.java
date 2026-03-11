package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@ActiveProfiles("test")
class FavoriteServiceTest {

    @Autowired
    private FavoriteService favoriteService;

    @MockBean
    private FavoriteRepository favoriteRepository;


    @Autowired
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("서비스 종료 시점에 UnexpectedRollbackException이 발생하는지 확인")
    void rollbackOnlyTest() {
        // 1. Given // Mock의 한계 -> JPA와 동일하게 지연쓰기로 동작하지 않음(즉 실제 운영환경과 다름)
        Favorite mockFavorite = Favorite.builder().id(1L).shop(new Shop()).build();
        given(favoriteRepository.findByShopIdAndMemberId(anyLong(), anyLong()))
                .willReturn(Optional.of(mockFavorite));

        doAnswer(invocation -> {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new org.springframework.dao.DataIntegrityViolationException("강제 에러");
        }).when(favoriteRepository).deleteById(anyLong());

        // 2. When & Then
        // transactionTemplate을 써서 cancel 메서드가 끝나는 즉시 커밋을 시도하게 만듦.
        assertThrows(UnexpectedRollbackException.class, () -> {
            transactionTemplate.execute(status -> {
                favoriteService.cancel(1L, 1L);
                return null;

            });
        });
    }
}