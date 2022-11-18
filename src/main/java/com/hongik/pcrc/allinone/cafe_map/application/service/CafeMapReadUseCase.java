package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

public interface CafeMapReadUseCase {

    List<HashMap<String, Object>> getCafeList();
    List<HashMap<String, Object>> getStationList();
    List<FindCafeSearchResult> searchCafe(CafeSearchEnum searchEnum, String cafe, String region, String category);

    @Getter
    @ToString
    @Builder
    class FindCafeSearchResult {
        private final int cafe_id;
        private final String cafe_name;
        private final String cafe_branch;
        private final String road_addr;
        private final String floor_info;
        private final double longitude;
        private final double latitude;

        public static FindCafeSearchResult findByCafeSearchResult(HashMap<String, Object> list, String floor_info) {
            return FindCafeSearchResult.builder()
                    .cafe_id((Integer) list.get("cafe_id"))
                    .cafe_name((String) list.get("cafe_name"))
                    .cafe_branch((String) list.get("cafe_branch"))
                    .road_addr((String) list.get("road_addr"))
                    .floor_info(floor_info)
                    .longitude((Double) list.get("longitude"))
                    .latitude((Double) list.get("latitude"))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindCafeResult {
    }

    @Getter
    @ToString
    @Builder
    class FindStationResult {
    }
}
