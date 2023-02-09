package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
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
import java.util.stream.Collectors;

@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/")
    public ResponseEntity<RsData> showFavoritesList(@AuthenticationPrincipal MemberContext memberContext) {
        List<FavoriteResponseDto> favoriteResponseDtos;

        List<Favorite> favorites = favoriteService.findByMemberId(memberContext.getId());

        if(favorites.isEmpty() == false) {
            favoriteResponseDtos = favorites
                    .stream()
                    .map(favorite -> favoriteService.toDto(favorite))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(
                    new RsData<>(true, "찜 리스트 조회 성공", favoriteResponseDtos),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new RsData<>(true, "찜 리스트 조회 성공, 찜 리스트가 없는 사용자"),
                    HttpStatus.OK);
        }

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{shopId}")
    public ResponseEntity<RsData> addShopToFavorites(@PathVariable Long shopId,
                                                     @AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();

        favoriteService.save(shopId, member);

        return new ResponseEntity<>(
                new RsData<>(true, "찜 추가 성공"),
                HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value="/{shopId}")
    public ResponseEntity<RsData> cancelShopFromFavorites(@PathVariable Long shopId,
                                                          @AuthenticationPrincipal MemberContext memberContext){
        favoriteService.cancel(shopId, memberContext.getId());

        return new ResponseEntity<>(
                new RsData<>(true, "찜 취소 성공"),
                HttpStatus.OK);
    }
}
