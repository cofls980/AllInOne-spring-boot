package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapperRepository {

    List<BoardReadUseCase.FindBoardResult> getList();

    BoardReadUseCase.FindMapperOneBoardResult getPost(int board_id);

    Board havePost(int board_id);

    void post(Board board);

    void update(Board board);

    void delete(int board_id);

    List<BoardReadUseCase.FindBoardResult> searchWriter(String b_writer);

    List<BoardReadUseCase.FindBoardResult> searchTitle(String title);

    List<BoardReadUseCase.FindBoardResult> searchBothWriterTitle(String keyword);

    int isUserLikes(Map<String, Object> map);

    void createLikes(Map<String, Object> map);

    void deleteLikes(Map<String, Object> map);

}
