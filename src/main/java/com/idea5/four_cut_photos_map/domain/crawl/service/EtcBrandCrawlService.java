package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
@SuperBuilder
@Transactional
abstract class EtcBrandCrawlService {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;
    public static final int PHOTO_STREET_TOTAL_PAGE = 5;
    public static final int PHOTO_LAP_PLUS_TOTAL_PAGE = 8;
    public static final int PLAY_IN_THE_BOX_TOTAL_PAGE = 3;
    public static final String INS_PHOTO_URL = "https://insphoto.co.kr/locations/";
    public static final String SELPIX_URL = "http://m.selpix.co.kr/shop_add_page/index.htm?page_code=page16&me_popup=1";
    public static final String PHOTO_STREET_URL = "https://photostreet.co.kr/?page_id=930&mode=list&board_page=";
    public static final String PHOTO_DRINK_URL = "https://photodrink.com/LOCATION";
    public static final String PHOTO_LAP_PLUS_URL = "https://www.photolabplus.co.kr/location1-";
    public static final String PLAY_IN_THE_BOX_URL = "https://www.playinthebox.co.kr/39/?sort=NAME&keyword_type=all&page=";
    public static final String HARRY_PHOTO_URL = "http://www.harryphoto.co.kr/";

    protected abstract void crawl();
    public Document connectToUrl(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
            }
        } catch (HttpStatusException e) {
            try {
                if (url.contains("photolapplus")) {
                    url = url.replaceFirst("/location\\d+-", "/1-");
                    doc = Jsoup.connect(url).get();
                } else {
                    log.error("Failed to connect to the URL: {}", e.getMessage());
                }
            } catch (IOException ex) {
                log.error("Failed to connect to the URL: {}", ex.getMessage());
            }
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        }
        return doc;
    }

    public Elements selectElements(Document doc, String selector, String url) {
        Elements elements = doc.select(selector);
        if (elements == null || elements.isEmpty()) {
            log.warn("No elements were found with the specified selector: {}", url);
        }
        return elements;
    }

    public String formatAddress(String roadAddressName) {
        if (roadAddressName.contains("대한민국 ")) {
            roadAddressName.replace("대한민국 ", "");
        }

        // 상세주소 제거 방법 (1)
        if (roadAddressName.contains(",")) {
            roadAddressName = roadAddressName.split(",")[0];
        }

        // 상세주소 제거 방법 (2)
        // '로[공백], 길[공백]으로 끝나는 문자열 + 그 다음 숫자(ex.61, 68-1)' 형태로 주소가 끝나도록 가공
        if (roadAddressName.matches(".*\\d.*")) {
            String[] roadSuffixes = {"로 ", "길 "};
            for (String suffix : roadSuffixes) {
                int idx = roadAddressName.lastIndexOf(suffix);
                if (idx != -1) {
                    String numAfterSuffix = "";
                    String suffixPart = roadAddressName.substring(idx + suffix.length());
                    String[] tokens = suffixPart.split(" ");
                    for (String token : tokens) {
                        if (token.matches("\\d+.*")) {
                            numAfterSuffix = token;
                            if(numAfterSuffix.endsWith("-"))
                                break;
                        }
                    }
                    roadAddressName = roadAddressName.substring(0, idx + suffix.length()) + numAfterSuffix;
                    break;
                }
            }
        }

        return roadAddressName;
    }

    protected abstract String formatPlaceName(String placeName);

    public void saveOrUpdateShop(String placeName, String roadAddressName) {
        Shop oldShop = shopRepository.findByPlaceName(placeName).orElse(null);
        if (oldShop == null) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .roadAddressName(roadAddressName)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, roadAddressName:{}", placeName, roadAddressName);
        } else {
            if (!oldShop.getRoadAddressName().equals(roadAddressName)) {
                oldShop.setRoadAddressName(roadAddressName);
                log.info("merge >> id:{}, changed fields: {}", oldShop.getId(), roadAddressName);
            }
        }
    }

    public void createShopIfNotExists(String placeName) {
        if (!shopRepository.existsByPlaceName(placeName)) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .roadAddressName(null)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, roadAddressName:{}", placeName, null);
        }
    }

    public boolean isBranchNameWithSuffix(String placeName) {
        return placeName.endsWith("점");
    }
}
