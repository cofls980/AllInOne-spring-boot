package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapperRepository {

    public Board getPost(int id);

    public void post(Board board);

    public void update(Board board);

    public void delete(int id);
}
