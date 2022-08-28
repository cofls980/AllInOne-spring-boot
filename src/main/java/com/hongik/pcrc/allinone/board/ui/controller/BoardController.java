package com.hongik.pcrc.allinone.board.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.BoardOperationUseCase;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
import com.hongik.pcrc.allinone.board.ui.view.Board.BoardView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.board.ui.requestBody.BoardRequest;
import com.hongik.pcrc.allinone.board.ui.view.Board.BoardListView;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v2/boards")
public class BoardController {

    private final BoardReadUseCase boardReadUseCase;
    private final BoardOperationUseCase boardOperationUseCase;

    public BoardController(BoardReadUseCase boardReadUseCase, BoardOperationUseCase boardOperationUseCase) {
        this.boardReadUseCase = boardReadUseCase;
        this.boardOperationUseCase = boardOperationUseCase;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<BoardReadUseCase.FindBoardResult>>> boardsList(@RequestParam(value = "writer", required = false) String writer,
                                                                                              @RequestParam(value = "title", required = false) String title) {

        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardList(writer, title);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @GetMapping("/{board_id}")
    public ResponseEntity<ApiResponseView<BoardView>> selectOne(@PathVariable int board_id) {

        BoardReadUseCase.FindOneBoardResult result = boardReadUseCase.getOneBoard(board_id);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new BoardView(result)));
    }

    @PostMapping("")
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
    public ResponseEntity<ApiResponseView<SuccessView>> deletePost(@PathVariable int board_id) { //token email, db email

        boardOperationUseCase.deleteBoard(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PutMapping("/{board_id}/likes")
    public ResponseEntity<ApiResponseView<SuccessView>> increaseThumbs(@PathVariable int board_id) {

        boardOperationUseCase.increaseThumbs(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
