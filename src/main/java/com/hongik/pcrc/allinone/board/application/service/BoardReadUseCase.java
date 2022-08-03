package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardReadUseCase {

    List<FindBoardResult> getBoardList();

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class BoardFindQuery {
        private String title;
        private String contents;
        private String writer;
        private String writerEmail;

        public BoardFindQuery(String title, String contents, String writer, String writerEmail) {
            this.title = title;
            this.contents = contents;
            this.writer = writer;
            this.writerEmail = writerEmail;
        }
    }

    @Getter
    @ToString
    @Builder
    class FindBoardResult {
        // Board Info
        private final int id;
        private final String title;
        private final String contents;
        private final String writer;
        private final String writerEmail;
        private final LocalDateTime date;

        public static FindBoardResult findByBoard(Board board) {
            return FindBoardResult.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .contents(board.getContents())
                    .writer(board.getContents())
                    .writerEmail(board.getWriter_email())
                    .date(board.getDate())
                    .build();
        }
    }
}
