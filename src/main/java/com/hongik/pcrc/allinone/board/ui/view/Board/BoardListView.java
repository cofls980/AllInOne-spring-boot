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

    private final int id;
    private final String title;
    private final String contents;
    private final String writer;
    private final String writerEmail;
    private final LocalDateTime date;

    public BoardListView(BoardReadUseCase.FindBoardResult result) {
        this.id = result.getId();
        this.title = result.getTitle();
        this.contents = result.getContents();
        this.writer = result.getWriter();
        this.writerEmail = result.getWriterEmail();
        this.date = result.getDate();
    }

}
