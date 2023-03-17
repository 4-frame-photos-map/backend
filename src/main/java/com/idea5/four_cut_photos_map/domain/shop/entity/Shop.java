package com.idea5.four_cut_photos_map.domain.shop.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(indexes = {@Index(name = "address_index",columnList = "roadAddressName")})
@ToString(callSuper = true)
public class Shop extends BaseEntity {

    private String placeName; // 상점명
    private String roadAddressName; // 주소
    private Integer favoriteCnt; // 찜 수 // MySQL Integer == MySQL int


    // TODO: 상점 상세페이지뿐만 아니라 상점 리스트 페이지에도 favoriteCnt(찜 수)를 전송해야 할 지? 논의
    // 상점 리스팅에도 찜 수가 들어가야 한다면 shop 생성자 인수에 favoriteCnt도 추가 필요
    // 찜 수 기준으로도 리스팅 정렬이 필요하다면 리스팅 페이지에 찜 수 필드도 추가 필요할 것으로 생각됨
    public Shop(String placeName, String roadAddressName) {
        this.placeName = placeName;
        this.roadAddressName = roadAddressName;
    }
}
