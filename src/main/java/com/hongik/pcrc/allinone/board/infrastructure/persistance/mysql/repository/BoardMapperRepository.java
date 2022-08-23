package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapperRepository {

    public Board getPost(int id);

    public void post(Board board);

    public void update(Board board);

    public void delete(int id);

    public List<BoardReadUseCase.FindBoardResult> searchWriter(String b_writer);

    public List<BoardReadUseCase.FindBoardResult> searchTitle(String title);
    public void updateThumbs(Board board);
}
