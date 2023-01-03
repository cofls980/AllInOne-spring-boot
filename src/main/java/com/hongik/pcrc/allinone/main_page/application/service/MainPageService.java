package com.hongik.pcrc.allinone.main_page.application.service;

import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.cafe_map.application.service.AboutCategory;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MainPageService implements MainPageReadUseCase {
    private final CafeMapMapperRepository cafeMapMapperRepository;
    private final BoardMapperRepository boardMapperRepository;
    private final ChatMapperRepository chatMapperRepository;

    public MainPageService(CafeMapMapperRepository cafeMapMapperRepository,
                           BoardMapperRepository boardMapperRepository,
                           ChatMapperRepository chatMapperRepository) {
        this.cafeMapMapperRepository = cafeMapMapperRepository;
        this.boardMapperRepository = boardMapperRepository;
        this.chatMapperRepository = chatMapperRepository;
    }

    @Override
    public List<FindCafeListResult> mainCafeList() {
        String[] categories = AboutCategory.getType();
        List<FindCafeListResult> result = new ArrayList<>();
        List<HashMap<String, Object>> cafes = cafeMapMapperRepository.getListByCategory("");

        //평점 순 - 내림차순
        cafes.sort((o1, o2) -> {
            o1.putIfAbsent("total_rating", "0.0");
            o2.putIfAbsent("total_rating", "0.0");
            Double views1 = Double.parseDouble(o1.get("total_rating").toString());
            Double views2 = Double.parseDouble(o2.get("total_rating").toString());
            return views2.compareTo(views1);
        });

        for (String category : categories) {
            var cafes_category = selectCafe(cafes, category);
            List<FindCafeList> cafe_list = new ArrayList<>();

            for (int i = 0;i < 10;i++) {
                if (i == cafes_category.size()) {
                    break;
                }

                HashMap<String, Object> cafe = cafes_category.get(i);
                String str_floor = cafe.get("floor_info").toString();
                if (!Objects.equals(str_floor, "") && !str_floor.contains("층")) {
                    String floor_info = "";
                    int floor = Integer.parseInt(str_floor);
                    if (floor < 0) {
                        floor = -floor;
                        floor_info += "지하 ";
                    }
                    floor_info += (floor + "층");
                    cafe.put("floor_info", floor_info);
                }
                cafe_list.add(FindCafeList.findByCafeList(cafe));
            }

            result.add(FindCafeListResult.findByCafeListResult(category, cafe_list));
        }

        return result;
    }

    @Override
    public List<FindPostListResult> mainPostList() {
        List<HashMap<String, Object>> posts = boardMapperRepository.searchPosts(null, null, "n");
        List<FindPostListResult> result = new ArrayList<>();

        //조회수 순 - 내림차순
        posts.sort((o1, o2) -> {
            Integer views1 = Integer.parseInt(o1.get("views").toString());
            Integer views2 = Integer.parseInt(o2.get("views").toString());
            return views2.compareTo(views1);
        });

        for (int i = 0;i < 10;i++) {
            if (i == posts.size()) {
                break;
            }
            result.add(FindPostListResult.findByPostListResult(posts.get(i)));
        }

        return result;
    }

    @Override
    public List<FindChannelListResult> mainChannelList() {
        List<HashMap<String, Object>> channels =  chatMapperRepository.getChannelList(new ArrayList<>());
        List<FindChannelListResult> result = new ArrayList<>();

        //참가자 많은 순 - 내림차순
        channels.sort((o1, o2) -> {
            Integer views1 = Integer.parseInt(o1.get("number_of_users").toString());
            Integer views2 = Integer.parseInt(o2.get("number_of_users").toString());
            return views2.compareTo(views1);
        });

        for (int i = 0;i < 10;i++) {
            if (i == channels.size()) {
                break;
            }
            result.add(FindChannelListResult.findByChannelListResult(channels.get(i)));
        }

        return result;
    }

    private List<HashMap<String, Object>> selectCafe(List<HashMap<String, Object>> list, String category_name) {
        List<HashMap<String, Object>> result = new ArrayList<>();

        for (HashMap<String, Object> h : list) {
            if (valueCompare(h, category_name)) {
                result.add(h);
            }
        }
        return result;
    }

    private boolean valueCompare(HashMap<String, Object> hmap, String category_name) {//pair?
        String[] type = AboutCategory.getType();
        Integer[] values = new Integer[type.length];
        int comp = AboutCategory.getCategoryIndex(category_name);

        if (comp == -1) {
            return false;
        }
        for (int t = 0; t < type.length; t++) {
            values[t] = Integer.parseInt(hmap.get(type[t]).toString());
        }
        if (values[comp] == 0) { // 나중에 수정
            return false;
        }
        for (int t = 0; t < type.length; t++) {
            if (values[comp] < values[t]) {
                return false;
            }
        }
        return true;
    }
}
