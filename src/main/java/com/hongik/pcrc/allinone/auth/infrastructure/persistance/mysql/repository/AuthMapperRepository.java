package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface AuthMapperRepository {

    // user table
    boolean existsByEmail(@Param("email") String email);
    void updatePwd(@Param("id") String id, @Param("password") String password);
    void deleteByEmail(@Param("email") String email);
    void updateRefreshToken(@Param("id") String id, @Param("refresh_token") String refresh_token);
    String getUUIDByEmail(@Param("email") String email);
    String getFriendUUID(@Param("email") String email, @Param("name") String name);
    AuthEntity getFriendInfo(@Param("id") String id);

    // user friend table
    List<HashMap<String, Object>> getFriendList(@Param("user") String user);
    void addFriend(@Param("user1") String user1, @Param("user2") String user2);
    boolean existedComb(@Param("user1") String user1, @Param("user2") String user2);
    void deleteFriend(@Param("friend_id") int friend_id);
}