package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity.CafeReviewEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CafeReviewMapperRepository {

    void createReview(CafeReviewEntity entity);
}
