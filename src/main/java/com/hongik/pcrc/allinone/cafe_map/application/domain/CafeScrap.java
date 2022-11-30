package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class CafeScrap {
    private final int scrap_id;
    private final int cafe_id;
    private final String user_id;
}
