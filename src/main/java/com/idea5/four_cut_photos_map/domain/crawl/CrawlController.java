package com.idea5.four_cut_photos_map.domain.crawl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
@RequiredArgsConstructor
public class CrawlController {
    private final CrawlService crawlService;

    @GetMapping
    public void crawl() {
//        crawlService.lifefourcutsCrawl();
//        crawlService.harufilmCrawl();
//        crawlService.photoismCrawl();
        crawlService.photograyCrawl();
    }
}
