package com.hongik.pcrc.allinone.comments.application.service;

import com.hongik.pcrc.allinone.comments.application.domain.Comments;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentsReadUseCase {

    List<FindCommentResult> getCommentList();

    @Getter
    @ToString
    @Builder
    class FindCommentResult {

        private final int comment_id;
        private final String comment;
        private final String c_writer;
        private final LocalDateTime c_date;
    }

    public static FindCommentResult findByComment(Comments comments) {
        return FindCommentResult.builder()
                .comment_id(comments.getComment_id())
                .comment(comments.getComment())
                .c_writer(comments.getC_writer())
                .c_date(comments.getC_date())
                .build();
    }
}
