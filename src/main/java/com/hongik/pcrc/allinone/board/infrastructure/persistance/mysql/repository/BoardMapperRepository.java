package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapperRepository {

    List<BoardReadUseCase.FindBoardResult> getList();

    List<BoardReadUseCase.FindBoardResult> searchWriter(String b_writer);

    List<BoardReadUseCase.FindBoardResult> searchTitle(String title);

    List<BoardReadUseCase.FindBoardResult> searchBothWriterTitle(String keyword);

    void post(Board board);

    void update(Board board);

    void delete(int board_id);


}
