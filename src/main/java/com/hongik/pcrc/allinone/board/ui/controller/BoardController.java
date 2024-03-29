package com.hongik.pcrc.allinone.board.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.BoardOperationUseCase;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.board.ui.requestBody.BoardRequest;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/v2/boards")
@Api(tags = {"Board API"})
public class BoardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BoardReadUseCase boardReadUseCase;
    private final BoardOperationUseCase boardOperationUseCase;

    public BoardController(BoardReadUseCase boardReadUseCase, BoardOperationUseCase boardOperationUseCase) {
        this.boardReadUseCase = boardReadUseCase;
        this.boardOperationUseCase = boardOperationUseCase;
    }

    @GetMapping(value = "", produces = "application/json")
    @ApiOperation(value = "게시판 목록")
    public ResponseEntity<ApiResponseView<List<BoardReadUseCase.FindBoardResult>>> boardsList(@RequestParam(value = "all", required = false) String all,
                                                                                              @RequestParam(value = "title", required = false) String title,
                                                                                              @RequestParam(value = "writer", required = false) String writer,
                                                                                              HttpServletResponse response) {

        SearchEnum se;


        if ((all == null || all.isEmpty()) && (title == null || title.isEmpty()) && (writer == null || writer.isEmpty())) {
            logger.info("게시판 전체 목록");
            se = SearchEnum.NOTHING;
        } else if (!(all == null || all.isEmpty()) && (title == null || title.isEmpty()) && (writer == null || writer.isEmpty())) {
            logger.info("게시글 제목&작성자 검색");
            se = SearchEnum.ALL;
        } else if ((all == null || all.isEmpty()) && !(title == null || title.isEmpty()) && (writer == null || writer.isEmpty())) {
            logger.info("게시글 제목 검색");
            se = SearchEnum.TITLE;
        } else if ((all == null || all.isEmpty()) && (title == null || title.isEmpty()) && !(writer == null || writer.isEmpty())){
            logger.info("게시글 작성자 검색");
            se = SearchEnum.WRITER;
        } else {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        String[] query_info = {all, title, writer};
        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardList(se, query_info);

        if (result == null || result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        response.setHeader("Count-Posts", String.valueOf(result.size()));

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @GetMapping(value = "/{board_id}", produces = "application/json")
    @ApiOperation(value = "게시글 선택")
    public ResponseEntity<ApiResponseView<BoardReadUseCase.FindOneBoardResult>> selectOne(@PathVariable int board_id) {

        logger.info("게시글 선택");
        BoardReadUseCase.FindOneBoardResult result = boardReadUseCase.getOneBoard(board_id);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @PostMapping(value = "", produces = "application/json")
    @ApiOperation(value = "게시글 작성")
    public ResponseEntity<ApiResponseView<SuccessView>> createPost(@Valid @RequestBody BoardRequest request) {

        logger.info("게시글 작성");
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

    @PutMapping(value = "/{board_id}", produces = "application/json")
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<ApiResponseView<SuccessView>> editPost(@Valid @RequestBody BoardRequest request, @PathVariable int board_id) { //token email, request email, db email

        logger.info("게시글 수정");
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

    @DeleteMapping(value = "/{board_id}", produces = "application/json")
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> deletePost(@PathVariable int board_id) {

        logger.info("게시글 삭제");
        boardOperationUseCase.deleteBoard(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PostMapping(value = "/{board_id}/likes", produces = "application/json")
    @ApiOperation(value = "게시글 좋아요")
    public ResponseEntity<ApiResponseView<SuccessView>> increaseLikes(@PathVariable int board_id) {

        logger.info("게시글 좋아요");
        boardOperationUseCase.increaseLikes(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping(value = "/{board_id}/unlikes", produces = "application/json")
    @ApiOperation(value = "게시글 좋아요 취소")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteLikes(@PathVariable int board_id) {

        logger.info("게시글 좋아요 취소");
        boardOperationUseCase.deleteLikes(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
