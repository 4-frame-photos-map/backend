package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawl.Entity.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlayintheboxCrawlService extends EtcBrandCrawlService {

    public PlayintheboxCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start PlayInTheBox Crawling=======");
        runCrawler(
                CrawlTarget.PLAY_IN_THE_BOX.getUrl(),
                CrawlTarget.PLAY_IN_THE_BOX.getTotalPage(),
                "div.map_contents.inline-blocked",
                "a.map_link.blocked div.head div.tit",
                "div.p_group p:first-child"
        );
        log.info("=======End PlayInTheBox Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return placeName;
    }
}
