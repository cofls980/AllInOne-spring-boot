package com.hongik.pcrc.allinone.comments.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CommentsOperationUseCase {

    void createComment(CommentCreatedCommand command);
    void updateComment(CommentUpdateCommand command);
    void deleteComment(int board_id, int comment_id);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CommentCreatedCommand {
        private final int board_id;
        private final String comment;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CommentUpdateCommand {
        private final int comment_id;
        private final int board_id;
        private final String comment;
    }
}
