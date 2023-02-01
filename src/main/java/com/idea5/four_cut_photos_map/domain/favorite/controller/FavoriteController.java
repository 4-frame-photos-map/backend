package com.idea5.four_cut_photos_map.domain.favorite.controller;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.member.service.MemberService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.MEMBER_MISMATCH;

@RequestMapping("/favorites")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final MemberService memberService;
    private final FavoriteService favoriteService;

    @GetMapping(value = "/{member-id}")
    public RsData<List<FavoriteResponseDto>> showFavoritesList(@PathVariable Long memberId, @AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberService.findById(memberId);

        //Todo: memberContext null 검사 해야 하는지 질문

        if (memberContext.memberIsNot(member)) {
            throw new BusinessException(MEMBER_MISMATCH);
        }
        List<FavoriteResponseDto> favoriteResponseDtos = member.getFavorites()
                .stream()
                .map(favorite -> favoriteService.toDto(favorite))
                .collect(Collectors.toList());

        return new RsData<>(true, "즐겨찾기 리스트 조회 성공", favoriteResponseDtos);
    }
}
