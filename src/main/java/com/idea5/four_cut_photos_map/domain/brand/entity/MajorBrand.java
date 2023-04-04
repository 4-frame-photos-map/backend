package com.idea5.four_cut_photos_map.domain.brand.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorBrand {
    LIFEFOURCUTS("인생네컷", "http://~~~"),
    HARUFILM("하루필름", "http://~~~"),
    PHOTOISM("포토이즘", "http://~~~"),
    PHOTOGRAY("포토그레이", "http://~~~");

    private String brandName;
    private String filePath;
}
