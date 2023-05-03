package com.idea5.four_cut_photos_map.domain.crawler.controller;

import com.idea5.four_cut_photos_map.domain.crawler.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
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
    private final LifefourcutsCrawlService lifefourcutsCrawlService;
    private final HarufilmCrawlService harufilmCrawlService;
    private final PhotoismCrawlService photoismCrawlService;
    private final PhotograyCrawlService photograyCrawlService;
    private final PhotosignatureCrawlService photosignatureCrawlService;

    @PostMapping("/main/shops")
    public ResponseEntity<String> crawlMainBrands() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        lifefourcutsCrawlService.crawl();
        harufilmCrawlService.crawl();
        photoismCrawlService.crawl();
        photograyCrawlService.crawl();

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        return ResponseEntity.ok("Main brands crawled successfully");
    }

    @PostMapping("/other/shops")
    public ResponseEntity<String> crawlOtherBrands() {

        photosignatureCrawlService.crawl();
        harryphotoCrawlService.crawl();
        insphotoCrawlService.crawl();
        photodrinkCrawlService.crawl();
        photolabplusCrawlService.crawl();
        photostreetCrawlService.crawl();
        playintheboxCrawlService.crawl();
        selpixCrawlService.crawl();

        return ResponseEntity.ok("Other brands crawled successfully");
    }

    @PostMapping("/insphoto/shops")
    public ResponseEntity<String> crawlInspohto() {
        insphotoCrawlService.crawl();
        return ResponseEntity.ok("Inspohto crawled successfully");
    }

    @PostMapping("/harryphoto/shops")
    public ResponseEntity<String> crawlHarryphoto() {
        harryphotoCrawlService.crawl();
        return ResponseEntity.ok("Harryphoto crawled successfully");
    }

    @PostMapping("/photodrink/shops")
    public ResponseEntity<String> crawlPhotodrink() {
        photodrinkCrawlService.crawl();
        return ResponseEntity.ok("Photodrink crawled successfully");
    }

    @PostMapping("/photolabplus/shops")
    public ResponseEntity<String> crawlPhotolabplus() {
        photolabplusCrawlService.crawl();
        return ResponseEntity.ok("Photolabplus crawled successfully");
    }

    @PostMapping("/photostreet/shops")
    public ResponseEntity<String> crawlPhotostreet() {
        photostreetCrawlService.crawl();
        return ResponseEntity.ok("Photostreet crawled successfully");
    }

    @PostMapping("/playinthebox/shops")
    public ResponseEntity<String> crawlPlayinthebox() {
        playintheboxCrawlService.crawl();
        return ResponseEntity.ok("Playinthebox crawled successfully");
    }

    @PostMapping("/selpix/shops")
    public ResponseEntity<String> crawlSelpix() {
        selpixCrawlService.crawl();
        return ResponseEntity.ok("Selpix crawled successfully");
    }
}
