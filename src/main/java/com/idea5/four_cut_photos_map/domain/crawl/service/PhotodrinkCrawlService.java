package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawl.Entity.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PhotodrinkCrawlService extends EtcBrandCrawlService {

    public PhotodrinkCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start PhotoDrink Crawling=======");
        try {
            String url = CrawlTarget.PHOTO_DRINK.getUrl();
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, "div.text-table div", url);

            for (Element element : elements) {
                Elements pElement = element.select("p");
                String placeName = pElement.get(0).select("span").text();

                if (isBranchNameWithSuffix(placeName)) {
                    String roadAddressName = pElement.get(1).select("span").text();

                    placeName = formatPlaceName(placeName);
                    roadAddressName = formatAddress(roadAddressName);

                    saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("=======End PhotoDrink Crawling=======");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    @Override
    protected String formatPlaceName(String placeName) {
        // 두번째 공백이 있다면 첫번째 공백 이후부터 두번째 공백 시작까지 제거
        // 세번째 공백이 있다면 세번째 공백 제거
        int firstIndex = placeName.indexOf(" ");
        if (firstIndex != -1) {
            int secondIndex = placeName.indexOf(" ", firstIndex + 1);
            if (secondIndex != -1) {
                placeName = placeName.substring(0, firstIndex + 1) + placeName.substring(secondIndex + 1);
                int thirdIndex = placeName.indexOf(" ", secondIndex + 1);
                if (thirdIndex != -1) {
                    placeName = placeName.substring(0, secondIndex + 1) + placeName.substring(thirdIndex + 1);
                }
            }
        }
        return placeName;
    }
}
