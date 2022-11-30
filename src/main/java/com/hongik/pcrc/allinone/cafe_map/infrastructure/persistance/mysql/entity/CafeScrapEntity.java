package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.cafe_map.application.domain.CafeScrap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "cafe_map_scrap")
public class CafeScrapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scrap_id;
    @Column(nullable = false)
    private int cafe_id;
    @Column(nullable = false, columnDefinition = "char(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private String user_id;

    public CafeScrapEntity(CafeScrap cafeScrap) {
        this.scrap_id = cafeScrap.getScrap_id();
        this.cafe_id = cafeScrap.getCafe_id();
        this.user_id = cafeScrap.getUser_id();
    }
}
