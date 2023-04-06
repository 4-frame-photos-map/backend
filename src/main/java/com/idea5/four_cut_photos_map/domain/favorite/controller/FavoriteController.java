package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.request.FavoriteRequest;
import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponse;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "")
    public ResponseEntity<List<FavoriteResponse>> showFavoritesList(@AuthenticationPrincipal MemberContext memberContext,
                                                                    @ModelAttribute @Valid FavoriteRequest favoriteRequest,
                                                                    @RequestParam(required = false, defaultValue = "created", value = "sort")
                                                                        String criteria) {

        List<FavoriteResponse> favoriteResponses = favoriteService.getFavoritesList(
                memberContext.getId(),
                criteria,
                favoriteRequest.getLongitude(),
                favoriteRequest.getLatitude()
        );

        return ResponseEntity.ok(favoriteResponses);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{shopId}")
    public void addShopToFavorites(@PathVariable Long shopId,
                                   @AuthenticationPrincipal MemberContext memberContext) {

        favoriteService.save(shopId, memberContext.getMember());

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/{shopId}")
    public void cancelShopFromFavorites(@PathVariable Long shopId,
                                        @AuthenticationPrincipal MemberContext memberContext) {
        favoriteService.cancel(shopId, memberContext.getId());

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크
    }
}