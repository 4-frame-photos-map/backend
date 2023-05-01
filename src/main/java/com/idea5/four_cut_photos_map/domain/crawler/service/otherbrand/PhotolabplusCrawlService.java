package com.idea5.four_cut_photos_map.domain.crawler.service.otherbrand;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.enums.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PhotolabplusCrawlService extends EtcBrandCrawlService {

    public PhotolabplusCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl(){
        log.info("=======Start PhotoLabPlus Crawling=======");
        runCrawler(
                CrawlTarget.PHOTO_LAB_PLUS.getUrl(),
                CrawlTarget.PHOTO_LAB_PLUS.getTotalPage(),
                "div.Zc7IjY",
                "h2 span.wixui-rich-text__text",
                "p span.wixui-rich-text__text"
        );
        log.info("=======End PhotoLabPlus Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return "포토랩플러스 " + placeName;
    }
}
