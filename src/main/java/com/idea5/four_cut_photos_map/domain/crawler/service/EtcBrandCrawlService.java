package com.idea5.four_cut_photos_map.domain.crawler.service;

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
                    String address = element.select(addressSelector).text();
                    address = formatAddress(address);
                    placeName = formatPlaceName(placeName);
                    saveOrUpdateShop(placeName, address);
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
                        String address = element.select(addressSelector).text();
                        address = formatAddress(address);
                        placeName = formatPlaceName(placeName);
                        saveOrUpdateShop(placeName, address);
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

    public static String formatAddress(String address) {
        if (address.startsWith("대한민국")) {
            address = address.replace("대한민국 ", "");
        }

        // 상세주소 제거 방법 (1)
        if (address.contains(",")) {
            address = address.split(",")[0];
        }

        // 상세주소 제거 방법 (2)
        // '로[공백], 길[공백]으로 끝나는 문자열 + 그 다음 공백 문자열(번호)' 형태로 주소가 끝나도록 가공
        if (address.matches(".*\\d.*")) {
            String[] addressSuffixes = {"로 ", "길 ", "동 "};
            for (String suffix : addressSuffixes) {
                int idx = address.lastIndexOf(suffix);
                if (idx != -1) {
                    String numAfterSuffix = address.substring(idx + suffix.length()).split(" ")[0];
                    address = address.substring(0, idx + suffix.length()) + numAfterSuffix;
                    break;
                }
            }
        }

        return address;
    }

    protected abstract String formatPlaceName(String placeName);

    public void saveOrUpdateShop(String placeName, String address) {
        Shop oldShop = shopRepository.findByPlaceName(placeName).orElse(null);
        if (oldShop == null) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .address(address)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, address:{}", placeName, address);
        } else {
            if (!oldShop.getAddress().equals(address)) {
                oldShop.setAddress(address);
                log.info("merge >> id:{}, changed fields: {}", oldShop.getId(), address);
            }
        }
    }

    public void createShopIfNotExists(String placeName) {
        if (!shopRepository.existsByPlaceName(placeName)) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .address(null)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, address:{}", placeName, null);
        }
    }

    public boolean isBranchNameWithSuffix(String placeName) {
        return placeName.endsWith("점");
    }
}
