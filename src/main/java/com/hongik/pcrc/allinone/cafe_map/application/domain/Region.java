package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Region {
    private final int region_id;
    private final String province;
    private final String city;
    private final double latitude;
    private final double longitude;
}
