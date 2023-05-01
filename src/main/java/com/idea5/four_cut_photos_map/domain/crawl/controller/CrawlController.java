package com.idea5.four_cut_photos_map.domain.crawl.controller;

import com.idea5.four_cut_photos_map.domain.crawl.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
@RequiredArgsConstructor
public class CrawlController {
    private final LifefourcutsCrawlService lifefourcutsCrawlService;
    private final HarufilmCrawlService harufilmCrawlService;
    private final PhotoismCrawlService photoismCrawlService;
    private final PhotograyCrawlService photograyCrawlService;
    private final PhotosignatureCrawlService photosignatureCrawlService;

    @GetMapping
    public void crawl() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        lifefourcutsCrawlService.crawl();
        harufilmCrawlService.crawl();
        photoismCrawlService.crawl();
        photograyCrawlService.crawl();
        photosignatureCrawlService.crawl();

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}