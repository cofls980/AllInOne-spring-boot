package com.hongik.pcrc.allinone.cafe_map.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class CafeReview {
    private final int review_id;
    private final int cafe_id;
    private final String user_id;
    private final LocalDateTime review_date;
    private final Double star_rating;
    private final String content;
    private final String photo;
    private final int like_number;
    private final String category_1;
    private final String category_2;
    private final String category_3;
}