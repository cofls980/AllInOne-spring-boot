package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Category {
    private final int category_id;
    private final int cafe_id;
    private final int store_num;
    private final int 큰규모;
    private final int 테이크아웃;
    private final int 소개팅;
    private final int 커피맛집;
    private final int 디저트맛집;
    private final int 인스타감성;
    private final int 조용한;
    private final int 데이트코스;
    private final int 드라이브;
    private final int 경치좋은;
    private final int 테마있는;
    private final int 공부맛집;
}
