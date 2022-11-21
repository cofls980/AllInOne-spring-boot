package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface CafeMapReviewReadUseCase {

    FindCafeInfoWithReviewResult getCafeInfoWithReview(int cafe_id);

    @Getter
    @ToString
    @Builder
    class FindCafeInfoWithReviewResult {
        private final String  cafe_name;
        private final String  cafe_branch;
        private final String  road_addr;
        private final String  floor_info;
        private final Double total_rating;
        private final String  category_1;
        private final String  category_2;
        private final String  category_3;
        private final List<FindCafeMapReviewListResult> reviews;

        public static FindCafeInfoWithReviewResult findByCafeReview(HashMap<String, Object> map,
                                                                    Double total_rating, String[] categories,
                                                                    List<FindCafeMapReviewListResult> reviews) {
            if (total_rating == null) {
                total_rating = 0.0;
            }
            return FindCafeInfoWithReviewResult.builder()
                    .cafe_name(map.get("cafe_name").toString())
                    .cafe_branch(map.get("cafe_branch").toString())
                    .road_addr(map.get("road_addr").toString())
                    .floor_info(map.get("floor_info").toString())
                    .total_rating(total_rating)
                    .category_1(categories[0])
                    .category_2(categories[1])
                    .category_3(categories[2])
                    .reviews(reviews)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindCafeMapReviewListResult {
        private final int review_id;
        private final String user_name;
        private final LocalDateTime review_date;
        private final Double star_rating;
        private final String content;
        private final String  category_1;
        private final String  category_2;
        private final String  category_3;

        public static FindCafeMapReviewListResult findByCafeReview(HashMap<String, Object> list, String user_name) {
            return FindCafeMapReviewListResult.builder()
                    .review_id((Integer) list.get("review_id"))
                    .user_name(user_name)
                    .review_date((LocalDateTime) list.get("review_date"))
                    .star_rating((Double) list.get("star_rating"))
                    .content(list.get("content").toString())
                    .category_1(list.get("category_1").toString())
                    .category_2(list.get("category_2").toString())
                    .category_3(list.get("category_3").toString())
                    .build();
        }
    }
}
