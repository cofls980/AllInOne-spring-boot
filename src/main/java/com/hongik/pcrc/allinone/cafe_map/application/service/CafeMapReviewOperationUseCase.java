package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

public interface CafeMapReviewOperationUseCase {

    void createReview(CafeMapReviewCreatedCommand command);
    void updateReview(CafeMapReviewUpdatedCommand command);
    void deleteReview(CafeMapReviewDeletedCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CafeMapReviewCreatedCommand {
        private final int cafe_id;
        private final double star_rating;
        private final String content;
//        private final MultipartFile photo;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CafeMapReviewUpdatedCommand {
        private final int review_id;
        private final int cafe_id;
        private final double star_rating;
        private final String content;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CafeMapReviewDeletedCommand {
        private final int review_id;
        private final int cafe_id;
    }
}
