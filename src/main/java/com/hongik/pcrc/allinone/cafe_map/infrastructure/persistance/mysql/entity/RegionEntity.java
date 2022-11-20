package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "cafe_map_region")
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int region_id;
    @Column(nullable = false)
    private String province;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
}
