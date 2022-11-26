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
@Table(name = "cafe_map_cafe")
public class CafeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cafe_id;
    @Column(nullable = false)
    private int store_num;
    @Column(nullable = false)
    private String cafe_name;
    private String cafe_branch;
    @Column(nullable = false)
    private String province;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String lot_number_addr;
    @Column(nullable = false)
    private String road_addr;
    @Column(nullable = false)
    private int old_postal_code;
    @Column(nullable = false)
    private int new_postal_code;
    private String floor_info;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
}
