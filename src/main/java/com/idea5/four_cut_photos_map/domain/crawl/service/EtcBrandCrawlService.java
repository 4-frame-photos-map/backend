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
import org.jsoup.nodes.Element;
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

    protected abstract void crawl();

    /**
     * 요청 페이지: 1
     * 크롤링 항목: 장소명
     * @param url
     * @param elementsSelector
     * @param placeNameSelector
     */
    public void runCrawler(String url, String elementsSelector, String placeNameSelector) {
        try {
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, elementsSelector, url);
            for (Element element : elements) {
                String placeName = element.select(placeNameSelector).text();
                if (isBranchNameWithSuffix(placeName)) {
                    placeName = formatPlaceName(placeName);
                    createShopIfNotExists(placeName);
                }
            }
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    /**
     * 요청 페이지: 1
     * 크롤링 항목: 장소명, 주소
     * @param url
     * @param elementsSelector
     * @param placeNameSelector
     * @param addressSelector
     */
    public void runCrawler(String url, String elementsSelector, String placeNameSelector, String addressSelector) {
        try {
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, elementsSelector, url);
            for (Element element : elements) {
                String placeName = element.selectFirst(placeNameSelector).text();
                if (isBranchNameWithSuffix(placeName)) {
                    String roadAddressName = element.select(addressSelector).text();
                    roadAddressName = formatAddress(roadAddressName);
                    placeName = formatPlaceName(placeName);
                    saveOrUpdateShop(placeName, roadAddressName);
                }
            }
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    /**
     * 요청 페이지: 다수
     * 크롤링 항목: 장소명, 주소
     * @param baseUrl
     * @param maxPageNum
     * @param elementsSelector
     * @param placeNameSelector
     * @param addressSelector
     */
    public void runCrawler(String baseUrl, int maxPageNum, String elementsSelector, String placeNameSelector, String addressSelector) {
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = connectToUrl(url);
                Elements elements = selectElements(doc,elementsSelector, url);

                for (Element element : elements) {
                    String placeName = element.select(placeNameSelector).text();
                    if (isBranchNameWithSuffix(placeName)) {
                        String roadAddressName = element.select(addressSelector).text();
                        roadAddressName = formatAddress(roadAddressName);
                        placeName = formatPlaceName(placeName);
                        saveOrUpdateShop(placeName, roadAddressName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    /**
     * 요청 페이지: 다수
     * 크롤링 항목: 장소명
     * @param baseUrl
     * @param maxPageNum
     * @param elementsSelector
     * @param placeNameSelector
     */
    public void runCrawler(String baseUrl, int maxPageNum, String elementsSelector, String placeNameSelector) {
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = connectToUrl(url);
                Elements elements = selectElements(doc,elementsSelector, url);
                for (Element element : elements) {
                    String placeName = element.selectFirst(placeNameSelector).text();
                    if (isBranchNameWithSuffix(placeName)) {
                        placeName = formatPlaceName(placeName);
                        createShopIfNotExists(placeName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    public Document connectToUrl(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
            }
        } catch (HttpStatusException e) {
            try {
                if (url.contains("photolabplus")) {
                    url = url.replaceFirst("/location\\d+-", "/1-");
                    doc = Jsoup.connect(url).get();
                } else {
                    log.warn("Failed to connect to the URL: {}", e.getMessage());
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
        if (roadAddressName.startsWith("대한민국")) {
            roadAddressName = roadAddressName.replace("대한민국 ", "");
        }

        // 상세주소 제거 방법 (1)
        if (roadAddressName.contains(",")) {
            roadAddressName = roadAddressName.split(",")[0];
        }

        // 상세주소 제거 방법 (2)
        // '로[공백], 길[공백]으로 끝나는 문자열 + 그 다음 공백 문자열(번호)' 형태로 주소가 끝나도록 가공
        if (roadAddressName.matches(".*\\d.*")) {
            String[] roadSuffixes = {"로 ", "길 ", "동 "};
            for (String suffix : roadSuffixes) {
                int idx = roadAddressName.lastIndexOf(suffix);
                if (idx != -1) {
                    String numAfterSuffix = roadAddressName.substring(idx + suffix.length()).split(" ")[0];
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
