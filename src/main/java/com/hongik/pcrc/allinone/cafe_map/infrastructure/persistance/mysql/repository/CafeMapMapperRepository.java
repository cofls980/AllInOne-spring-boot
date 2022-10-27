package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CafeMapMapperRepository {

    // cafe
    List<HashMap<String, Object>> getCafeList();

    // station
    List<HashMap<String, Object>> getStationList();
}
