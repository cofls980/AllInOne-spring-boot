package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface BoardMapperRepository {

    // Search
    List<HashMap<String, Object>> searchPosts(@Param("user_id") String user_id, @Param("list") ArrayList<String> list, @Param("type") String type);
    HashMap<String, Object> getOnePost(@Param("board_id") int board_id, @Param("user_id") String user_id);

    // Check
    boolean notExistedPostWithWriter(@Param("board_id") int board_id, @Param("user_id") String user_id);
    boolean notExistedPost(@Param("board_id") int board_id);

    // CUD
    void post(Board board);
    void update(Board board);
    void delete(int board_id);
}
