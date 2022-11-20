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
    public List<FindCafeSearchResult> searchCafe(CafeSearchEnum searchEnum, String cafe_name, String province, String city, String category) {

        List<HashMap<String, Object>> list = null;
        if (searchEnum == CafeSearchEnum.CAFE) {
            list =  cafeMapMapperRepository.getListByCafeName(cafe_name);
        } else if (searchEnum == CafeSearchEnum.REGION) {
            list = cafeMapMapperRepository.getListByRegion(province, city);
        } else if (searchEnum == CafeSearchEnum.CAFEANDREGION) {
            list = cafeMapMapperRepository.getListByCafeNameAndRegion(cafe_name, province, city);
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

    @Override
    public List<FindRegionList> getRegionInfo() {

        List<HashMap<String, Object>> list = cafeMapMapperRepository.getRegionInfo();
        List<FindRegionResult> result = new ArrayList<>();
        List<FindRegionList> res = new ArrayList<>();
        String comp = "";
        for (HashMap<String, Object> h : list) {
            if (comp.equals("")) {
                comp = h.get("province").toString();
            }
            if (!comp.equals(h.get("province").toString())) {
                res.add(FindRegionList.findByRegionResult(comp, result));
                result = new ArrayList<>();
                comp = h.get("province").toString();
            }
            result.add(FindRegionResult.findByRegionResult(h));
        }
        res.add(FindRegionList.findByRegionResult(comp, result));
        return res;
    }
}
