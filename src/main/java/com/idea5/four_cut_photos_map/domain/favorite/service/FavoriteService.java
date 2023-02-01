package com.idea5.four_cut_photos_map.domain.favorite.service;

import com.idea5.four_cut_photos_map.domain.favorite.dto.response.FavoriteResponseDto;
import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    public FavoriteResponseDto toDto(Favorite favorite) {

        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .member(favorite.getMember())
                .shop(favorite.getShop())
                .build();
    }
}
