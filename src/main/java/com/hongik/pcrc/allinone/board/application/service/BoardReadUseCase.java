package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface BoardReadUseCase {

    List<FindBoardResult> getBoardList(SearchEnum searchEnum, String[] query_info);
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

        public static FindBoardResult findByBoard(HashMap<String, Object> b) {
            boolean click = b.get("res") != null;
            return FindBoardResult.builder()
                    .board_id((Integer) b.get("board_id"))
                    .title(b.get("title").toString())
                    .content(b.get("content").toString())
                    .b_writer(b.get("name").toString())
                    .b_date((LocalDateTime) b.get("b_date"))
                    .likes(Integer.parseInt(b.get("likes").toString()))
                    .click_likes(click)
                    .views(Integer.parseInt(b.get("views").toString()))
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

        public static FindOneBoardResult findByOneBoard(HashMap<String, Object> b,
                                                        List<CommentsReadUseCase.FindCommentResult> comments) {
            boolean click = b.get("res") != null;
            return FindOneBoardResult.builder()
                    .board_id((Integer) b.get("board_id"))
                    .title((String) b.get("title"))
                    .content((String) b.get("content"))
                    .email((String) b.get("email"))
                    .b_writer((String) b.get("name"))
                    .b_date((LocalDateTime) b.get("b_date"))
                    .likes(Integer.parseInt(b.get("likes").toString()))
                    .click_likes(click)
                    .views(Integer.parseInt(b.get("views").toString()))
                    .commentList(comments)
                    .build();
        }
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