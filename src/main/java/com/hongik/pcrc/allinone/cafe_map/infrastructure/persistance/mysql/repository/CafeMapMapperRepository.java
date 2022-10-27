package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CafeMapMapperRepository {

    // cafe
    List<CafeMapReadUseCase.FindCafeResult> getCafeList();

    // station
    List<CafeMapReadUseCase.FindStationResult> getStationList();
}
