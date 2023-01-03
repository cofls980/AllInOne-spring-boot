package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CafeMapService implements CafeMapOperationUseCase, CafeMapReadUseCase{

    private final CafeMapMapperRepository cafeMapMapperRepository;
    private final AuthMapperRepository authMapperRepository;

    public CafeMapService(CafeMapMapperRepository cafeMapMapperRepository,
                          AuthMapperRepository authMapperRepository) {
        this.cafeMapMapperRepository = cafeMapMapperRepository;
        this.authMapperRepository = authMapperRepository;
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
            if (!h.get("floor_info").toString().isEmpty()) {
                String floor_info = "";
                int floor = Integer.parseInt(h.get("floor_info").toString());
                if (floor < 0) {
                    floor = -floor;
                    floor_info += "지하 ";
                }
                floor_info += (floor + "층");
                h.put("floor_info", floor_info);
            }
            h.putIfAbsent("total_rating", "0.0");
            result.add(FindCafeSearchResult.findByCafeSearchResult(h, AboutCategory.getTop3(h)));
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
            for (String t : AboutCategory.getType()) {
                if (valueCompare(h, t)) {
                    map.put(t, map.get(t) + 1);
                }
            }
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

    // Scrap
    @Override
    public void createScrap(CafeMapScrapCreatedCommand command) {
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        if (cafeMapMapperRepository.isExistedScrap(command.getCafe_id(), user_id)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        cafeMapMapperRepository.createScrap(command.getCafe_id(), user_id);
    }

    @Override
    public List<FindCategoryScrapList> getScrap() {
        //user_id -> cafe_id&scrap_id in scrap table -> cafe_info
        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);
        List<HashMap<String, Object>> list = cafeMapMapperRepository.getScrap(user_id);
        List<FindCategoryScrapList> result = new ArrayList<>();

        for (String type : AboutCategory.getType()) {
            List<HashMap<String, Object>> selected = selectCafe(list, type);
            List<FindScraps> scraps = new ArrayList<>();
            for (HashMap<String, Object> h : selected) {
                scraps.add(FindScraps.findByScrapsResult(h));
            }
            result.add(FindCategoryScrapList.findByCategoryScrapResult(type, scraps));
        }

        return result;
    }

    @Override
    public void deleteScrap(CafeMapScrapDeletedCommand command) {
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        if (!cafeMapMapperRepository.canDeleteScrap(command.getScrap_id(),command.getCafe_id(), user_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        cafeMapMapperRepository.deleteScrap(command.getScrap_id());
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
