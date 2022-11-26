package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeReviewMapperRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CafeMapService implements CafeMapOperationUseCase, CafeMapReadUseCase{

    private final CafeMapMapperRepository cafeMapMapperRepository;
    private final CafeReviewMapperRepository cafeReviewMapperRepository;

    public CafeMapService(CafeMapMapperRepository cafeMapMapperRepository,
                          CafeReviewMapperRepository cafeReviewMapperRepository) {
        this.cafeMapMapperRepository = cafeMapMapperRepository;
        this.cafeReviewMapperRepository = cafeReviewMapperRepository;
    }

    @Override
    public List<FindCafeSearchResult> searchCafe(CafeSearchEnum searchEnum, String cafe_name, String province, String city, String category) {

        List<HashMap<String, Object>> list;
        if (searchEnum == CafeSearchEnum.CAFE) {
            list =  cafeMapMapperRepository.getListByCafeName(cafe_name);
        } else if (searchEnum == CafeSearchEnum.REGION) {
            list = cafeMapMapperRepository.getListByRegion(province, city);
        } else if (searchEnum == CafeSearchEnum.CATEGORY) {
            list = cafeMapMapperRepository.getListByCategory(category);
            list = selectCafe(list, category);
        } else if (searchEnum == CafeSearchEnum.CAFEANDREGION) {
            list = cafeMapMapperRepository.getListByCafeNameAndRegion(cafe_name, province, city);
        } else if (searchEnum == CafeSearchEnum.CAFEANDCATEGORY) {
            list =  cafeMapMapperRepository.getListByCafeName(cafe_name);
            list = selectCafe(list, category);
        } else if (searchEnum == CafeSearchEnum.REGIONANDCATEGORY) {
            list = cafeMapMapperRepository.getListByRegion(province, city);
            list = selectCafe(list, category);
        } else {
            list = cafeMapMapperRepository.getListByCafeNameAndRegion(cafe_name, province, city);
            list = selectCafe(list, category);
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
            System.out.println(cafeReviewMapperRepository.getTotalRating((Integer) h.get("cafe_id")));
            Double total_rating = cafeReviewMapperRepository.getTotalRating((Integer) h.get("cafe_id"));
            result.add(FindCafeSearchResult.findByCafeSearchResult(h, total_rating, floor_info, AboutCategory.getTop3(h)));
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

    @Override
    public List<FindCategoryList> getCategoryInfo() {
        List<HashMap<String, Object>> list = cafeMapMapperRepository.getCategoryInfo();
        HashMap<String, Integer> map = new HashMap<>();
        List<FindCategoryList> result = new ArrayList<>();

        for (String t : AboutCategory.getType()) {
            map.put(t, 0);
        }
        for (HashMap<String, Object> h : list) {
            String[] top3 = AboutCategory.getTop3(h);
            map.put(top3[0], map.get(top3[0]) + 1);
        }
        for (String t : AboutCategory.getType()) {
            result.add(FindCategoryList.findByCategoryResult(t, map.get(t)));
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
