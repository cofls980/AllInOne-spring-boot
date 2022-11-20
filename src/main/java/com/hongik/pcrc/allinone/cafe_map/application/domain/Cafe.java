package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;

@Getter
@ToString
@Builder
public class Cafe {
    private final int cafe_id;
    private final int store_num;
    private final String cafe_name;
    private final String cafe_branch;
    private final String province;
    private final String city;
    private final String lot_number_addr;
    private final String road_addr;
    private final int old_postal_code;
    private final int new_postal_code;
    private final String floor_info;
    private final double latitude;
    private final double longitude;
}