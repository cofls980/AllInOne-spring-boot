package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<FindCafeSearchResult> searchCafe(CafeSearchEnum searchEnum, String cafe, String region, String category) {

        List<HashMap<String, Object>> list = null;
        if (searchEnum == CafeSearchEnum.CAFE) {
            list =  cafeMapMapperRepository.getListByCafeName(cafe);
        } else {
            //list = chatMapperRepository.searchChannelListWithTitle(command.getTitle());
        }
        List<FindCafeSearchResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : list) {
            String floor_info = "";
            if (!h.get("floor_info").toString().isEmpty()) {
                int floor = Integer.parseInt(h.get("floor_info").toString());
                if (floor < 0) {
                    floor = -floor;
                    floor_info += "지하 ";
                }
                floor_info += (floor + "층");
            }
            result.add(FindCafeSearchResult.findByCafeSearchResult(h, floor_info));
        }
        return result;
    }
}
