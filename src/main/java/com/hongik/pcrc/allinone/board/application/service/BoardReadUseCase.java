package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardReadUseCase {

    List<FindBoardResult> getBoardList();
    FindOneBoardResult getOneBoard(int board_id);
    List<FindBoardResult> getBoardWriterList(String b_writer);
    List<FindBoardResult> getBoardTitleList(String title);

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class BoardFindQuery {
        private String title;
        private String content;
        private String writer;
        private String writerEmail;

        public BoardFindQuery(String title, String content, String writer, String writerEmail) {
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.writerEmail = writerEmail;
        }
    }

    @Getter
    @ToString
    @Builder
    class FindBoardResult {
        // Board Info
        private final int board_id;
        private final String title;
        private final String b_writer;
        private final LocalDateTime b_date;
        private final int thumbs_up;

        public static FindBoardResult findByBoard(Board board) {
            return FindBoardResult.builder()
                    .board_id(board.getBoard_id())
                    .title(board.getTitle())
                    .b_writer(board.getContent())
                    .b_date(board.getB_date())
                    .thumbs_up(board.getThumbs_up())
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindOneBoardResult {
        // Board Info
        private final int board_id;
        private final String title;
        private final String content;
        private final String b_writer;
        private final LocalDateTime b_date;
        private final int thumbs_up;
        private final List<CommentsReadUseCase.FindCommentResult> commentList;

        public static FindBoardResult findByBoard(Board board) {
            return FindBoardResult.builder()
                    .board_id(board.getBoard_id())
                    .title(board.getTitle())
                    .b_writer(board.getContent())
                    .b_date(board.getB_date())
                    .thumbs_up(board.getThumbs_up())
                    .build();
        }
    }
}
