package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyPageMapperRepository {
    // mypage
    void updateProfile(@Param("user_id") String user_id, @Param("profile_path") String profile_path);
    void createDefaultProfile(@Param("user_id") String user_id, @Param("profile_path") String profile_path);
    String getProfilePath(@Param("user_id") String user_id);
}
