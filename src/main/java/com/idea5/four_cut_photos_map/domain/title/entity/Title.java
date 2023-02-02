package com.idea5.four_cut_photos_map.domain.title.entity;

import com.idea5.four_cut_photos_map.global.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Title extends BaseEntity {

    private String name; // 칭호명

    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TitleLog> titleLogs = new ArrayList<>();
}
