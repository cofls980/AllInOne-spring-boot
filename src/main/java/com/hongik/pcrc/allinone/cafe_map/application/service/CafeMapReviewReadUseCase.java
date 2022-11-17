package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity.CafeReviewEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface CafeMapReviewReadUseCase {

    List<FindCafeMapReviewListResult> getCafeMapReviewList(int cafe_id);

    @Getter
    @ToString
    @Builder
    class FindCafeMapReviewListResult {
        private final int review_id;
        private final String user_name;
        private final LocalDateTime review_date;
        private final Double star_rating;
        private final String content;

        public static FindCafeMapReviewListResult findByCafeReview(HashMap<String, Object> list, String user_name) {
            return FindCafeMapReviewListResult.builder()
                    .review_id((Integer) list.get("review_id"))
                    .user_name(user_name)
                    .review_date((LocalDateTime) list.get("review_date"))
                    .star_rating((Double) list.get("star_rating"))
                    .content(list.get("content").toString())
                    .build();
        }
    }
}
