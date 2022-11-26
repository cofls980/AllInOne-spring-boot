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
    List<FindCategoryList> getCategoryInfo();

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
        private final Double total_rating;
        private final String category_1;
        private final String category_2;
        private final String category_3;

        public static FindCafeSearchResult findByCafeSearchResult(HashMap<String, Object> list, Double total_rating,
                                                                  String floor_info, String[] top3) {
            if (total_rating == null) {
                total_rating = 0.0;
            }
            return FindCafeSearchResult.builder()
                    .cafe_id((Integer) list.get("cafe_id"))
                    .cafe_name((String) list.get("cafe_name"))
                    .cafe_branch((String) list.get("cafe_branch"))
                    .road_addr((String) list.get("road_addr"))
                    .floor_info(floor_info)
                    .latitude((Double) list.get("latitude"))
                    .longitude((Double) list.get("longitude"))
                    .total_rating(total_rating)
                    .category_1(top3[0])
                    .category_2(top3[1])
                    .category_3(top3[2])
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

    @Getter
    @ToString
    @Builder
    class FindCategoryList {
        private final String category_name;
        private final int category_cafe_num;

        public static FindCategoryList findByCategoryResult(String cn, int ccn) {
            return FindCategoryList.builder()
                    .category_name(cn)
                    .category_cafe_num(ccn)
                    .build();
        }
    }
}
