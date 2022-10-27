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
@Table(name = "station")
public class StationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subway_id;
    @Column(nullable = false)
    private int station_id;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String district;
    @Column(nullable = false)
    private int line;
    @Column(nullable = false)
    private String station_name;
    @Column(nullable = false)
    private String lot_number_addr;
    @Column(nullable = false)
    private String road_addr;
}
