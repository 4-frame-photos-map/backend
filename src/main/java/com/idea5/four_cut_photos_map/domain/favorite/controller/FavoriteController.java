package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final ShopService shopService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "")
    public ResponseEntity<List<FavoriteResponse>> showFavoritesList(@AuthenticationPrincipal MemberContext memberContext,
                                                                    @RequestParam @NotNull Double userLat,
                                                                    @RequestParam @NotNull Double userLng
                                                                    ) {

        List<FavoriteResponse> favoriteResponses = favoriteService.getFavoritesList(memberContext.getId(), userLat, userLng);

        return ResponseEntity.ok(favoriteResponses);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{shopId}")
    public void addShopToFavorites(@PathVariable Long shopId,
                                   @AuthenticationPrincipal MemberContext memberContext) {

        Shop shop = favoriteService.save(shopId, memberContext.getMember());

        shopService.increaseFavoriteCnt(shop);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/{shopId}")
    public void cancelShopFromFavorites(@PathVariable Long shopId,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Shop shop = favoriteService.cancel(shopId, memberContext.getId());

        shopService.reduceFavoriteCnt(shop);
    }
}