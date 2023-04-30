package com.idea5.four_cut_photos_map.domain.crawler.controller;

import com.idea5.four_cut_photos_map.domain.crawler.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
class CrawlController {
    private final HarryphotoCrawlService harryphotoCrawlService;
    private final InsphotoCrawlService insphotoCrawlService;
    private final PhotodrinkCrawlService photodrinkCrawlService;
    private final PhotolabplusCrawlService photolabplusCrawlService;
    private final PhotostreetCrawlService photostreetCrawlService;
    private final PlayintheboxCrawlService playintheboxCrawlService;
    private final SelpixCrawlService selpixCrawlService;

    @PostMapping("/etc/shops")
    public ResponseEntity<String> crawlAllEtcBrandShops() {

        harryphotoCrawlService.crawl();
        insphotoCrawlService.crawl();
        photodrinkCrawlService.crawl();
        photolabplusCrawlService.crawl();
        photostreetCrawlService.crawl();
        playintheboxCrawlService.crawl();
        selpixCrawlService.crawl();

        return ResponseEntity.ok("All etc brands crawled successfully");
    }

    @PostMapping("/insphoto/shops")
    public ResponseEntity<String> crawlInspohtoShops() {
        insphotoCrawlService.crawl();
        return ResponseEntity.ok("Inspohto crawled successfully");
    }

    @PostMapping("/harryphoto/shops")
    public ResponseEntity<String> crawlHarryphotoShops() {
        harryphotoCrawlService.crawl();
        return ResponseEntity.ok("Harryphoto crawled successfully");
    }

    @PostMapping("/photodrink/shops")
    public ResponseEntity<String> crawlPhotodrinkShops() {
        photodrinkCrawlService.crawl();
        return ResponseEntity.ok("Photodrink crawled successfully");
    }

    @PostMapping("/photolabplus/shops")
    public ResponseEntity<String> crawlPhotolabplusShops() {
        photolabplusCrawlService.crawl();
        return ResponseEntity.ok("Photolabplus crawled successfully");
    }

    @PostMapping("/photostreet/shops")
    public ResponseEntity<String> crawlPhotostreetShops() {
        photostreetCrawlService.crawl();
        return ResponseEntity.ok("Photostreet crawled successfully");
    }

    @PostMapping("/playinthebox/shops")
    public ResponseEntity<String> crawlPlayintheboxShops() {
        playintheboxCrawlService.crawl();
        return ResponseEntity.ok("Playinthebox crawled successfully");
    }

    @PostMapping("/selpix/shops")
    public ResponseEntity<String> crawlSelpixShops() {
        selpixCrawlService.crawl();
        return ResponseEntity.ok("Selpix crawled successfully");
    }
}
