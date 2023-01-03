package com.hongik.pcrc.allinone.main_page.application.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface MainPageReadUseCase {

    List<FindCafeListResult> mainCafeList();
    List<FindPostListResult> mainPostList();
    List<FindChannelListResult> mainChannelList();

    @Getter
    @ToString
    @Builder
    class FindCafeList {
        private final int cafe_id;
        private final String cafe_name;
        private final String cafe_branch;
        private final String road_addr;
        private final String floor_info;
        private final double total_rating;

        public static FindCafeList findByCafeList(HashMap<String, Object> list) {
            return FindCafeList.builder()
                    .cafe_id((Integer) list.get("cafe_id"))
                    .cafe_name((String) list.get("cafe_name"))
                    .cafe_branch((String) list.get("cafe_branch"))
                    .road_addr((String) list.get("road_addr"))
                    .floor_info((String) list.get("floor_info"))
                    .total_rating(Double.parseDouble(list.get("total_rating").toString()))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindCafeListResult {
        private final String category_name;
        private final List<FindCafeList> cafe_list;

        public static FindCafeListResult findByCafeListResult(String category_name,
                                                              List<FindCafeList> cafe_list) {
            return FindCafeListResult.builder()
                    .category_name(category_name)
                    .cafe_list(cafe_list)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindPostListResult {
        private final int board_id;
        private final String title;
        private final String b_writer;
        private final LocalDateTime b_date;
        private final int likes;
        private final int views;

        public static FindPostListResult findByPostListResult(HashMap<String, Object> list) {
            return FindPostListResult.builder()
                    .board_id((Integer) list.get("board_id"))
                    .title((String) list.get("title"))
                    .b_writer((String) list.get("b_writer"))
                    .b_date((LocalDateTime) list.get("b_date"))
                    .likes(Integer.parseInt(list.get("likes").toString()))
                    .views(Integer.parseInt(list.get("views").toString()))
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindChannelListResult {
        private final int channel_id;
        private final String ch_title;
        private final LocalDateTime created_date;
        private final int number_of_users;

        public static FindChannelListResult findByChannelListResult(HashMap<String, Object> list) {
            return FindChannelListResult.builder()
                    .channel_id((Integer) list.get("channel_id"))
                    .ch_title((String) list.get("ch_title"))
                    .created_date((LocalDateTime) list.get("created_date"))
                    .number_of_users(Integer.parseInt(list.get("number_of_users").toString()))
                    .build();
        }
    }
}
