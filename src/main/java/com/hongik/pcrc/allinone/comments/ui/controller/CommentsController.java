package com.hongik.pcrc.allinone.comments.ui.controller;

import com.hongik.pcrc.allinone.comments.application.service.CommentsOperationUseCase;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import com.hongik.pcrc.allinone.comments.ui.requestBody.CommentsRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Signature;

@RestController
@RequestMapping(value = "/v2/boards")
@Api(tags = {"Comment API"})
public class CommentsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CommentsOperationUseCase commentsOperationUseCase;


    public CommentsController(CommentsOperationUseCase commentsOperationUseCase) {
        this.commentsOperationUseCase = commentsOperationUseCase;
    }

    @PostMapping(value = "/{board_id}/comments", produces = "application/json")
    @ApiOperation(value = "댓글 작성")
    public ResponseEntity<ApiResponseView<SuccessView>> writeComment(@Valid @RequestBody CommentsRequest request, @PathVariable int board_id) {

        logger.info("댓글 작성");
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

    @DeleteMapping(value = "/{board_id}/comments/{comment_id}", produces = "application/json")
    @ApiOperation(value = "댓글 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteComment(@PathVariable int board_id, @PathVariable int comment_id) {

        logger.info("댓글 삭제");
        commentsOperationUseCase.deleteComment(board_id, comment_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PutMapping(value = "/{board_id}/comments/{comment_id}", produces = "application/json")
    @ApiOperation(value = "댓글 수정")
    public ResponseEntity<ApiResponseView<SuccessView>> updateComment(@Valid @RequestBody CommentsRequest request, @PathVariable int board_id, @PathVariable int comment_id) {

        logger.info("댓글 수정");
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
