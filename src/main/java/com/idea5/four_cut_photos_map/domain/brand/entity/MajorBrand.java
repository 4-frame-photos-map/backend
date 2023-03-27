package com.idea5.four_cut_photos_map.domain.brand.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorBrand {
    HARUFILM("하루필름", "대표이미지경로"),
    LIFEFOURCUTS("인생네컷", "대표이미지경로"),
    PHOTOISM("포토이즘", "대표이미지경로"),
    PHOTOGRAY("포토그레이", "대표이미지경로"),
    NO_BRAND("없음", "기본이미지경로");

    private String brandName;
    private String filePath;
}
