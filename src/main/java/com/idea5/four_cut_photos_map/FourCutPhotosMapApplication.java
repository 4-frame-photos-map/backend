package com.idea5.four_cut_photos_map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // @CreatedDate, @LastModifiedDate 기능 사용
public class FourCutPhotosMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(FourCutPhotosMapApplication.class, args);
    }

}
