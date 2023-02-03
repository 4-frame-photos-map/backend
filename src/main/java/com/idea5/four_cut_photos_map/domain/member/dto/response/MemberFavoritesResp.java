package com.idea5.four_cut_photos_map.domain.member.dto.response;

import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberFavoritesResp {
    private Long id;
    private Long kakaoId;
    private String nickname;
    private List<Favorite> favorites;
}
