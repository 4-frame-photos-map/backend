package com.idea5.four_cut_photos_map.security.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AccessToken {
    private String accessToken;
}
