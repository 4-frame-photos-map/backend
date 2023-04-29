package com.idea5.four_cut_photos_map.domain.crawl.Entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CrawlTarget {

    INS_PHOTO("https://insphoto.co.kr/locations/", null),
    SELPIX("http://m.selpix.co.kr/shop_add_page/index.htm?page_code=page16&me_popup=1", null),
    PHOTO_STREET("https://photostreet.co.kr/?page_id=930&mode=list&board_page=", 5),
    PHOTO_DRINK("https://photodrink.com/LOCATION", null),
    PHOTO_LAP_PLUS("https://www.photolabplus.co.kr/location1-", 8),
    PLAY_IN_THE_BOX("https://www.playinthebox.co.kr/39/?sort=NAME&keyword_type=all&page=", 3),
    HARRY_PHOTO("http://www.harryphoto.co.kr/", null);

    private final String url;
    private final Integer totalPage;

    public String getUrl() {
        return url;
    }

    public Integer getTotalPage() {
        return totalPage;
    }
}
