package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity.CafeReviewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CafeReviewMapperRepository {

    void createReview(CafeReviewEntity entity);
    List<HashMap<String, Object>> cafeMapReviewList(@Param("cafe_id") int cafe_id);
    boolean isCorrectReview(@Param("review_id") int review_id, @Param("cafe_id") int cafe_id, @Param("user_id") String user_id);
    void updateReview(@Param("review_id") int review_id, @Param("star_rating") Double star_rating, @Param("content") String content);
    void deleteReview(@Param("review_id") int review_id);
}
