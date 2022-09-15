package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikesViewsMapperRepository {

    int isUserLikes(@Param("user_id") String user_id, @Param("board_id") int board_id);

    void createLikes(@Param("user_id") String user_id, @Param("board_id") int board_id);

    void deleteLikes(@Param("user_id") String user_id, @Param("board_id") int board_id);

    boolean checkView(@Param("user_id") String user_id, @Param("board_id") int board_id);

    void createView(@Param("user_id") String user_id, @Param("board_id") int board_id);
}
