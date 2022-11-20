package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import io.swagger.models.auth.In;
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
        } else if (searchEnum == CafeSearchEnum.CATEGORY) {
            list = cafeMapMapperRepository.getListByCategory(category);
            list = selectCafe(list, category);
        } else if (searchEnum == CafeSearchEnum.CAFEANDREGION) {
            list = cafeMapMapperRepository.getListByCafeNameAndRegion(cafe_name, province, city);
        } else if (searchEnum == CafeSearchEnum.CAFEANDCATEGORY) {
            //list = cafeMapMapperRepository.getListByCafeNameAndCategory(cafe_name, category);
        } else if (searchEnum == CafeSearchEnum.REGIONANDCATEGORY) {
            //list = cafeMapMapperRepository.getListByRegionAndCategory(province, city, category);
        } else {
            //list = cafeMapMapperRepository.getListByALL(cafe_name, province, city, category);
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
            HashMap<String, Object> categories = cafeMapMapperRepository.getCategoriesAboutCafe((Integer) h.get("cafe_id"));
            result.add(FindCafeSearchResult.findByCafeSearchResult(h, floor_info, AboutCategory.getTop3(categories)));
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
        HashMap<String, Object> map = cafeMapMapperRepository.getCategoryInfo();
        List<FindCategoryList> result = new ArrayList<>();
        if (!map.isEmpty()) {
            for (String t : AboutCategory.getType()) {
                result.add(FindCategoryList.findByCategoryResult(t, Integer.parseInt(map.get(t).toString())));
            }
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
