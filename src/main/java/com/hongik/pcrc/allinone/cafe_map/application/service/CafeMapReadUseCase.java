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
    List<FindCategoryScrapList> getScrap();

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

        public static FindCafeSearchResult findByCafeSearchResult(HashMap<String, Object> list,
                                                                  String[] top3) {
            return FindCafeSearchResult.builder()
                    .cafe_id((Integer) list.get("cafe_id"))
                    .cafe_name((String) list.get("cafe_name"))
                    .cafe_branch((String) list.get("cafe_branch"))
                    .road_addr((String) list.get("road_addr"))
                    .floor_info((String) list.get("floor_info"))
                    .latitude((Double) list.get("latitude"))
                    .longitude((Double) list.get("longitude"))
                    .total_rating(Double.parseDouble(list.get("total_rating").toString()))
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

    @Getter
    @ToString
    @Builder
    class FindScraps {
        private final int scrap_id;
        private final int cafe_id;
        private final String cafe_name;
        private final String cafe_branch;
        private final String road_addr;

        public static FindScraps findByScrapsResult(HashMap<String, Object> map) {
            return FindScraps.builder()
                    .scrap_id((Integer) map.get("scrap_id"))
                    .cafe_id((Integer) map.get("cafe_id"))
                    .cafe_name((String) map.get("cafe_name"))
                    .cafe_branch((String) map.get("cafe_branch"))
                    .road_addr((String) map.get("road_addr"))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindCategoryScrapList {
        private final String category_name;
        private final List<FindScraps> scrap_list;

        public static FindCategoryScrapList findByCategoryScrapResult(String category_name,
                                                            List<FindScraps> scrap_list) {
            return FindCategoryScrapList.builder()
                    .category_name(category_name)
                    .scrap_list(scrap_list)
                    .build();
        }
    }
}
