package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

public interface CafeMapReviewOperationUseCase {

    void createReview(CafeMapReviewCreatedCommand command);

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
}
