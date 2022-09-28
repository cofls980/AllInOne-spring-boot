package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardReadUseCase {

    List<FindBoardResult> getBoardList(SearchEnum searchEnum,
                                       String b_writer, String title, String all);
    FindOneBoardResult getOneBoard(int board_id);

    @Getter
    @ToString
    @Builder
    class FindBoardResult {
        // Board Info
        private final int board_id;
        private final String title;
        private final String content;
        private final String b_writer;
        private final LocalDateTime b_date;
        private final int likes;
        private final boolean click_likes;
        private final int views;

        public static FindBoardResult findByBoard(Board board) {
            return FindBoardResult.builder()
                    .board_id(board.getBoard_id())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .b_writer(board.getContent())
                    .b_date(board.getB_date())
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
        private final String email;
        private final String b_writer;
        private final LocalDateTime b_date;
        private final int likes;
        private final boolean click_likes;
        private final int views;
        private final List<CommentsReadUseCase.FindCommentResult> commentList;

        public static FindBoardResult findByBoard(Board board) {
            return FindBoardResult.builder()
                    .board_id(board.getBoard_id())
                    .title(board.getTitle())
                    .b_writer(board.getContent())
                    .b_date(board.getB_date())
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindMapperOneBoardResult {
        // Board Info
        private final int board_id;
        private final String title;
        private final String content;
        private final String user_id;
        private final LocalDateTime b_date;
        private final int likes;

    }
}


    /*@NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class BoardFindQuery {
        private String title;
        private String content;
        private String writer;
        private String user_id;

        public BoardFindQuery(String title, String content, String writer, String writerEmail) {
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.user_id = writerEmail;
        }
    }*/