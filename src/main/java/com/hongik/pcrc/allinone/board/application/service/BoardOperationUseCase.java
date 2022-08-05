package com.hongik.pcrc.allinone.board.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface BoardOperationUseCase {

    void createBoard(BoardCreatedCommand command);
    void updateBoard(BoardUpdateCommand command);
    void deleteBoard(int id, String userId);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class BoardCreatedCommand {
        private final String title;
        private final String contents;
        private final String writer;
        private final String writer_email;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class BoardUpdateCommand {
        private final int id;
        private final String title;
        private final String contents;
        private final String writer;
        private final String writer_email;
    }
}
