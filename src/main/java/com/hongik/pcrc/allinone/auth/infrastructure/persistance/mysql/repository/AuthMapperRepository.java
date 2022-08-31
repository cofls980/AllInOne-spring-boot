package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AuthMapperRepository {

    boolean existsByEmail(String email);

    void updatePwd(Map<String, Object> map);

    void deleteByEmail(String email);

    void updateRefreshToken(Map<String, Object> map);

}
