package com.idea5.four_cut_photos_map.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    ACCESS_TOKEN("ATK"), REFRESH_TOKEN("RTK");

    private String name;
}
