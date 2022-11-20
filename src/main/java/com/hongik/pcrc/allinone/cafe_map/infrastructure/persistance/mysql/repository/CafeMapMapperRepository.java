package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReadUseCase;
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
    HashMap<String, Object> getCategoriesAboutCafe(@Param("cafe_id") int cafe_id);
//    List<HashMap<String, Object>> getListByCafeNameAndCategory(@Param("cafe_name") String cafe_name, @Param("category") String category);
//    List<HashMap<String, Object>> getListByRegionAndCategory(@Param("province") String province, @Param("city") String city,
//                                                             @Param("category") String category);
//    List<HashMap<String, Object>> getListByALL(@Param("cafe_name") String cafe_name,
//                                               @Param("province") String province, @Param("city") String city,
//                                               @Param("category") String category);
    List<HashMap<String, Object>> getRegionInfo();
    HashMap<String, Object> getCategoryInfo();
    void increaseCategoryNum(HashMap<String, Integer> categories);

}
