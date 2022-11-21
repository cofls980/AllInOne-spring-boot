package com.hongik.pcrc.allinone.cafe_map.application.service;

import java.util.*;

public class AboutCategory {
    private static final String[] type = {"경치좋은", "공부맛집", "데이트코스", "드라이브", "디저트맛집", "소개팅", "인스타감성", "조용한", "커피맛집", "큰규모", "테마있는", "테이크아웃"};

    public static  String[] getType() {
        return type;
    }

    public static boolean isNotInCategories(String category_name) {
        return !Arrays.asList(type).contains(category_name);
    }

    public static Integer getCategoryIndex(String category_name) {
        for (int i = 0; i < 12; i++) {
            if (category_name.equals(type[i])) {
                return i;
            }
        }
        return -1;
    }

    public static HashMap<String, Integer> makeIncreasedValueMap(String[] categories, Integer cafe_id) {

        HashMap<String, Integer> map = new HashMap<>();

        map.put("cafe_id", cafe_id);
        for (String t : type) {
            map.put(t, 0);
        }
        for (String c : categories) {
            for (String t : type) {
                if (t.equals(c)) {
                    map.put(t, map.get(t) + 1);
                }
            }
        }
        return map;
    }

    public static HashMap<String, Integer> makeDecreasedValueMap(HashMap<String, Object> categories, Integer cafe_id) {
        HashMap<String, Integer> map = new HashMap<>();

        String[] arr = {(String) categories.get("category_1"), (String) categories.get("category_2"), (String) categories.get("category_3")};
        map.put("cafe_id", cafe_id);
        for (String t : type) {
            map.put(t, 0);
        }
        for (String c : arr) {
            for (String t : type) {
                if (t.equals(c)) {
                    map.put(t, map.get(t) - 1);
                }
            }
        }
        return map;
    }

    public static String[] getTop3(HashMap<String, Object> hmap) {

        // Object -> Integer
        HashMap<String, Integer> list = new HashMap<>();
        for (String s : type) {
            int val = Integer.parseInt(hmap.get(s).toString());
            list.put(s, val);
        }

        // Compare
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<>(list.entrySet());
        list_entries.sort((obj1, obj2) -> obj2.getValue().compareTo(obj1.getValue()));

        // get Top 3
        String[] result = new String[3];
        int i = 0;
        for(Map.Entry<String, Integer> entry : list_entries) {
            if (entry.getValue() == 0 || i == 3) {
                break;
            }
            result[i] = entry.getKey();
            i++;
        }
        return result;
    }
}
