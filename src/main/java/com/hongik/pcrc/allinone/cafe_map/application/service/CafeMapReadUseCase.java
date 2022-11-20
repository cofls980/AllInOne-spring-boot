package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

public interface CafeMapReadUseCase {

    List<FindCafeSearchResult> searchCafe(CafeSearchEnum searchEnum, String cafe,
                                          String province, String city, String category);
    List<FindRegionList> getRegionInfo();

    @Getter
    @ToString
    @Builder
    class FindCafeSearchResult {
        private final int cafe_id;
        private final String cafe_name;
        private final String cafe_branch;
        private final String road_addr;
        private final String floor_info;
        private final double latitude;
        private final double longitude;

        public static FindCafeSearchResult findByCafeSearchResult(HashMap<String, Object> list, String floor_info) {
            return FindCafeSearchResult.builder()
                    .cafe_id((Integer) list.get("cafe_id"))
                    .cafe_name((String) list.get("cafe_name"))
                    .cafe_branch((String) list.get("cafe_branch"))
                    .road_addr((String) list.get("road_addr"))
                    .floor_info(floor_info)
                    .latitude((Double) list.get("latitude"))
                    .longitude((Double) list.get("longitude"))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindRegionResult {
        private final int region_id;
        private final String province;
        private final String city;
        private final double latitude;
        private final double longitude;

        public static FindRegionResult findByRegionResult(HashMap<String, Object> list) {
            return FindRegionResult.builder()
                    .region_id((Integer) list.get("region_id"))
                    .province((String) list.get("province"))
                    .city((String) list.get("city"))
                    .latitude((Double) list.get("latitude"))
                    .longitude((Double) list.get("longitude"))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindRegionList {
        private final String province;
        private final List<FindRegionResult> list;

        public static FindRegionList findByRegionResult(String province, List<FindRegionResult> list) {
            return FindRegionList.builder()
                    .province(province)
                    .list(list)
                    .build();
        }
    }
}
