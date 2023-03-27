package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "")
    public ResponseEntity<RsData> showFavoritesList(@AuthenticationPrincipal MemberContext memberContext,
    @RequestParam(required = false, defaultValue = "created", value = "sort") String criteria) {

        List<FavoriteResponseDto> favoriteResponseDtos = favoriteService.getFavoritesList(memberContext.getId(), criteria);

        return new ResponseEntity<>(
                new RsData<>(true,
                        favoriteResponseDtos != null? "찜 목록 조회 성공":"찜 목록이 없는 사용자", favoriteResponseDtos),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{shopId}")
    public ResponseEntity<RsData> addShopToFavorites(@PathVariable Long shopId,
                                                     @AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();

        favoriteService.save(shopId, member);

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크

        return new ResponseEntity<>(
                new RsData<>(true, "찜 추가 성공"),
                HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value="/{shopId}")
    public ResponseEntity<RsData> cancelShopFromFavorites(@PathVariable Long shopId,
                                                          @AuthenticationPrincipal MemberContext memberContext){
        favoriteService.cancel(shopId, memberContext.getId());

        // todo: ShopTitle 관련 로직 임의로 주석 처리, 리팩토링 필요
//        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크

        return new ResponseEntity<>(
                new RsData<>(true, "찜 취소 성공"),
                HttpStatus.OK);
    }
}
