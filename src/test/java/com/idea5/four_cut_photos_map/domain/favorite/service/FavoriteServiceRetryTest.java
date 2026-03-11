package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.repository.FavoriteRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FavoriteServiceRetryTest {

    @Autowired
    private FavoriteService favoriteService;

    @MockBean
    private FavoriteRepository favoriteRepository;

    @Test
    @DisplayName("낙관적 락 예외 발생 시 최대 3번까지 재시도하고 성공한다")
    void retryOnOptimisticLockException() {
        ClassLoader classLoader = FavoriteRepository.class.getClassLoader();
        System.out.println("classLoader = " + classLoader);
        System.out.println("classLoader.getParent() = " + classLoader.getParent());
        System.out.println("classLoader.getParent().getParent() = " + classLoader.getParent().getParent());

        // Given
        Long shopId = 1L;
        Long memberId = 1L;
        Long favoriteId = 100L;

        Shop mockShop = new Shop();
        Favorite mockFavorite = Favorite.builder().id(favoriteId).shop(mockShop).build();

        when(favoriteRepository.findByShopIdAndMemberId(shopId, memberId))
                .thenReturn(Optional.of(mockFavorite));

        doThrow(new ObjectOptimisticLockingFailureException(Favorite.class, favoriteId))
                .doNothing()
                .when(favoriteRepository).delete(any(Favorite.class));

        // When
        favoriteService.cancel(shopId, memberId);

        // Then
        verify(favoriteRepository, times(2)).delete(any(Favorite.class));
        verify(favoriteRepository, times(2)).findByShopIdAndMemberId(shopId, memberId);
    }

    @Test
    @DisplayName("3번 모두 예외가 발생하면 결국 예외를 던진다")
    void retryFailureAfterMaxAttempts() {
        // Given
        Long favoriteId = 100L;
        Favorite mockFavorite = Favorite.builder().id(favoriteId).build();

        when(favoriteRepository.findByShopIdAndMemberId(anyLong(), anyLong()))
                .thenReturn(Optional.of(mockFavorite));

        doThrow(new ObjectOptimisticLockingFailureException(Favorite.class, favoriteId))
                .when(favoriteRepository).delete(any(Favorite.class));

        // When & Then
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            favoriteService.cancel(1L, 1L);
        });

        verify(favoriteRepository, times(3)).delete(any(Favorite.class));
    }
}