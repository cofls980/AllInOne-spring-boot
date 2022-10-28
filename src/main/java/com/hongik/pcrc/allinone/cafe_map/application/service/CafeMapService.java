package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CafeMapService implements CafeMapOperationUseCase, CafeMapReadUseCase{

    private final CafeMapMapperRepository cafeMapMapperRepository;

    public CafeMapService(CafeMapMapperRepository cafeMapMapperRepository) {
        this.cafeMapMapperRepository = cafeMapMapperRepository;
    }

    @Override
    public List<HashMap<String, Object>> getCafeList() {
        return cafeMapMapperRepository.getCafeList();
    }

    @Override
    public List<HashMap<String, Object>> getStationList() {
        return cafeMapMapperRepository.getStationList();
    }
}
