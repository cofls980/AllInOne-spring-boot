package com.hongik.pcrc.allinone.board.ui.view.Board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardView {
    private final int board_id;
    private final String title;
    private final String content;
    private final String b_writer;
    private final LocalDateTime b_date;
    private final int likes;
    private final List<CommentsReadUseCase.FindCommentResult> commentList;

    public BoardView(BoardReadUseCase.FindOneBoardResult result) {
        this.board_id = result.getBoard_id();
        this.title = result.getTitle();
        this.content = result.getContent();
        this.b_writer = result.getB_writer();
        this.b_date = result.getB_date();
        this.likes = result.getLikes();
        this.commentList = result.getCommentList();
    }
}
