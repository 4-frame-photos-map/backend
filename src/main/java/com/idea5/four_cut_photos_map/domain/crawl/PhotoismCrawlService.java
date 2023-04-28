package com.idea5.four_cut_photos_map.domain.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PhotoismCrawlService implements CrawlService {
    public void crawl() {
        // [지점코드, 페이지]
        int[][] infos = {
                {279, 32}, // 포토이즘 박스
                {280, 6},   // 포토이즘 컬러드
        };
        for(int[] info : infos) {
            int cnt = 0;
            for(int i = 1; i <= info[1]; i++) {
                String url = "https://photoism.co.kr/" + info[0] + "/?sort=TIME&keyword_type=all&page=" + i;
                Connection conn = Jsoup.connect(url);

                try {
                    Document document = conn.get();
                    Elements titles = document.select("div.map_container.clearfix.map-inner._map_container");

                    for (Element e : titles) {
                        String placeName = e.select("div.tit").text().trim();
                        System.out.println(placeName);
                        String address = e.select("p.adress").text().trim();
                        System.out.println(address);
                        cnt++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("cnt = " + cnt);
        }
    }
}
