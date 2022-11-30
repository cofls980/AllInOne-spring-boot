package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CafeMapMapperRepository {

    boolean isExistedCafe(@Param("cafe_id") int cafe_id);
    List<HashMap<String, Object>> getListByCafeName(@Param("cafe_name") String cafe_name);
    List<HashMap<String, Object>> getListByRegion(@Param("province") String province, @Param("city") String city);
    List<HashMap<String, Object>> getListByCategory(@Param("category") String category);
    List<HashMap<String, Object>> getListByCafeNameAndRegion(@Param("cafe_name") String cafe_name,
                                                             @Param("province") String province, @Param("city") String city);
    List<HashMap<String, Object>> getRegionInfo();
    List<HashMap<String, Object>> getCategoryInfo();
    void changeCategoryNum(HashMap<String, Integer> categories);

    // Scrap
    boolean isExistedScrap(@Param("cafe_id") int cafe_id, @Param("user_id") String user_id);
    boolean canDeleteScrap(@Param("scrap_id") int scrap_id, @Param("cafe_id") int cafe_id, @Param("user_id") String user_id);
    void createScrap(@Param("cafe_id") int cafe_id, @Param("user_id") String user_id);
    void deleteScrap(@Param("scrap_id") int scrap_id);
    List<HashMap<String, Object>> getScrap(@Param("user_id") String user_id);
    void deleteAllUserScrap(@Param("user_id") String user_id);
}
