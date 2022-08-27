package com.hongik.pcrc.allinone.board.ui.view.Board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardListView {

    private final int board_id;
    private final String title;
    private final String b_writer;
    private final LocalDateTime b_date;
    private final int thumbs_up;

    public BoardListView(BoardReadUseCase.FindBoardResult result) {
        this.board_id = result.getBoard_id();
        this.title = result.getTitle();
        this.b_writer = result.getB_writer();
        this.b_date = result.getB_date();
        this.thumbs_up = result.getThumbs_up();
    }

}
