package com.hongik.pcrc.allinone.board.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.BoardOperationUseCase;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import com.hongik.pcrc.allinone.board.ui.requestBody.BoardViewsRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.board.ui.requestBody.BoardRequest;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/boards")
@Api(tags = {"Board API"})
public class BoardController {

    private final BoardReadUseCase boardReadUseCase;
    private final BoardOperationUseCase boardOperationUseCase;

    public BoardController(BoardReadUseCase boardReadUseCase, BoardOperationUseCase boardOperationUseCase) {
        this.boardReadUseCase = boardReadUseCase;
        this.boardOperationUseCase = boardOperationUseCase;
    }

    @GetMapping("")
    @ApiOperation(value = "게시판 목록")
    public ResponseEntity<ApiResponseView<List<BoardReadUseCase.FindBoardResult>>> boardsList(@RequestParam(value = "writer", required = false) String writer,
                                                                                              @RequestParam(value = "title", required = false) String title) {

        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardList(writer, title);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @GetMapping("/{board_id}")
    @ApiOperation(value = "게시글 선택")
    public ResponseEntity<ApiResponseView<BoardReadUseCase.FindOneBoardResult>> selectOne(@PathVariable int board_id) {

        BoardReadUseCase.FindOneBoardResult result = boardReadUseCase.getOneBoard(board_id);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @PostMapping("")
    @ApiOperation(value = "게시글 작성")
    public ResponseEntity<ApiResponseView<SuccessView>> createPost(@RequestBody BoardRequest request) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = BoardOperationUseCase.BoardCreatedCommand.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        boardOperationUseCase.createBoard(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PutMapping("/{board_id}")
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<ApiResponseView<SuccessView>> editPost(@RequestBody BoardRequest request, @PathVariable int board_id) { //token email, request email, db email

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = BoardOperationUseCase.BoardUpdateCommand.builder()
                .id(board_id)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        boardOperationUseCase.updateBoard(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping("/{board_id}")
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> deletePost(@PathVariable int board_id) { //token email, db email

        boardOperationUseCase.deleteBoard(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PostMapping("/{board_id}/likes")
    @ApiOperation(value = "게시글 좋아요")
    public ResponseEntity<ApiResponseView<SuccessView>> increaseLikes(@PathVariable int board_id) {

        boardOperationUseCase.increaseLikes(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping("/{board_id}/unlikes")
    @ApiOperation(value = "게시글 좋아요 취소")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteLikes(@PathVariable int board_id) {

        boardOperationUseCase.deleteLikes(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PutMapping("/views")
    @ApiOperation(value = "게시글 조회수 업데이트")
    public ResponseEntity<ApiResponseView<SuccessView>> updateView(@RequestBody List<BoardViewsRequest> request) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        boardOperationUseCase.updateViews(request);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
