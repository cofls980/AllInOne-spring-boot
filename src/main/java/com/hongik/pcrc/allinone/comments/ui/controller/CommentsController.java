package com.hongik.pcrc.allinone.comments.ui.controller;

import com.hongik.pcrc.allinone.comments.application.service.CommentsOperationUseCase;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import com.hongik.pcrc.allinone.comments.ui.requestBody.CommentsRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Signature;

@RestController
@RequestMapping(value = "/v2/boards")
public class CommentsController {

    private final CommentsReadUseCase commentsReadUseCase;
    private final CommentsOperationUseCase commentsOperationUseCase;


    public CommentsController(CommentsReadUseCase commentsReadUseCase, CommentsOperationUseCase commentsOperationUseCase) {
        this.commentsReadUseCase = commentsReadUseCase;
        this.commentsOperationUseCase = commentsOperationUseCase;
    }

    @PostMapping("/{board_id}/comments")
    public ResponseEntity<ApiResponseView<SuccessView>> writeComment(@RequestBody CommentsRequest request, @PathVariable int board_id) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = CommentsOperationUseCase.CommentCreatedCommand.builder()
                .board_id(board_id)
                .comment(request.getComment())
                .build();

        commentsOperationUseCase.createComment(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping("/{board_id}/comments/{comment_id}")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteComment(@PathVariable int board_id, @PathVariable int comment_id) {

        commentsOperationUseCase.deleteComment(board_id, comment_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PutMapping("/{board_id}/comments/{comment_id}")
    public ResponseEntity<ApiResponseView<SuccessView>> updateComment(@RequestBody CommentsRequest request, @PathVariable int board_id, @PathVariable int comment_id) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = CommentsOperationUseCase.CommentUpdateCommand.builder()
                .comment_id(comment_id)
                .board_id(board_id)
                .comment(request.getComment())
                .build();

        commentsOperationUseCase.updateComment(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
