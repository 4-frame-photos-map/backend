package com.idea5.four_cut_photos_map.domain.crawler.service.mainbrand;

import org.springframework.stereotype.Service;

@Service
abstract class MainBrandCrawlService {
    protected abstract void crawl();

    public String formatAddress(String address) {
        if (address.contains(",")) {
            address = address.split(",")[0];
        }

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
}
