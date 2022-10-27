package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Station {
    private final int subway_id;
    private final int station_id;
    private final String city;
    private final String district;
    private final int line;
    private final String station_name;
    private final String lot_number_addr;
    private final String road_addr;
}