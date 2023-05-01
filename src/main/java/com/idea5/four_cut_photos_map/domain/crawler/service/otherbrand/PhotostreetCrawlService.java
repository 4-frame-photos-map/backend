package com.idea5.four_cut_photos_map.domain.crawler.service.otherbrand;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.enums.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class PhotostreetCrawlService extends EtcBrandCrawlService {

    public PhotostreetCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl(){
        log.info("=======Start PhotoStreet Crawling=======");
        runCrawler(
                CrawlTarget.PHOTO_STREET.getUrl(),
                CrawlTarget.PHOTO_STREET.getTotalPage(),
                "td.text-left a",
                "span"
        );
        log.info("=======End PhotoStreet Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        placeName = placeName.contains("(") ? placeName.split("\\(")[0] : placeName;

        // 지역명과 점포번호 분리
        if (!placeName.contains("오픈예정")) {
            Pattern[] regexes = {
                    Pattern.compile("\\d+호\\s+(.*)"),
                    Pattern.compile("\\d+호점\\s+(.*)")
            };
            for (Pattern regex : regexes) {
                Matcher matcher = regex.matcher(placeName);
                if (matcher.find()) {
                    placeName = "포토스트리트 " + matcher.group(1);
                    createShopIfNotExists(placeName);
                    break;
                }
            }
        }
        return placeName;
    }
}
