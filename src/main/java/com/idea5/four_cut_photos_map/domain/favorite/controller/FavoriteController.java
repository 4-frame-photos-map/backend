package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.DUPLICATE_FAVORITE;
import static com.idea5.four_cut_photos_map.global.error.ErrorCode.FAVORITES_NOT_FOUND;

@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "")
    public ResponseEntity<RsData> showFavoritesList(@AuthenticationPrincipal MemberContext memberContext) {

        List<FavoriteResponseDto> favoriteResponseDtos = favoriteService.findByMemberId(memberContext.getId());

        if(ObjectUtils.isEmpty(favoriteResponseDtos)) {
            throw new BusinessException(FAVORITES_NOT_FOUND);
        }

        return new ResponseEntity<>(
                new RsData<>(true, "찜 리스트 조회 성공", favoriteResponseDtos),
                HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{shopId}")
    public ResponseEntity<RsData> addShopToFavorites(@PathVariable Long shopId,
                                                     @AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();

        favoriteService.save(shopId, member);
        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크
        return new ResponseEntity<>(
                new RsData<>(true, "찜 추가 성공"),
                HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value="/{shopId}")
    public ResponseEntity<RsData> cancelShopFromFavorites(@PathVariable Long shopId,
                                                          @AuthenticationPrincipal MemberContext memberContext){
        favoriteService.cancel(shopId, memberContext.getId());
        favoriteService.isHotPlace(shopId); // 칭호부여 여부 체크
        return new ResponseEntity<>(
                new RsData<>(true, "찜 취소 성공"),
                HttpStatus.OK);
    }
}
