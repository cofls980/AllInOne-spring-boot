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
@RequestMapping(value = "/v1/board")
public class BoardController {

    private final BoardReadUseCase boardReadUseCase;
    private final BoardOperationUseCase boardOperationUseCase;

    public BoardController(BoardReadUseCase boardReadUseCase, BoardOperationUseCase boardOperationUseCase) {
        this.boardReadUseCase = boardReadUseCase;
        this.boardOperationUseCase = boardOperationUseCase;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseView<List<BoardListView>>> returnBoardList() {

        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardList();
        if (result == null)
            throw new AllInOneException(MessageType.NOT_FOUND);

        List<BoardListView> boardListViews = result.stream().map(BoardListView::new).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseView<>(boardListViews));
    }

    @GetMapping("/{board_id}")
    public ResponseEntity<ApiResponseView<BoardView>> showPost(@PathVariable int board_id) {

        BoardReadUseCase.FindOneBoardResult result = boardReadUseCase.getOneBoard(board_id);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new BoardView(result)));
    }

    @GetMapping("/search/writer/{writer}")
    public ResponseEntity<ApiResponseView<List<BoardReadUseCase.FindBoardResult>>> searchBoardsWithWriter(@PathVariable String writer) {

        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardWriterList(writer);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        //List<BoardListView> boardListViews = result.stream().map(BoardListView::new).collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<ApiResponseView<List<BoardReadUseCase.FindBoardResult>>> searchBoardsWithTitle(@PathVariable String title) {

        List<BoardReadUseCase.FindBoardResult> result = boardReadUseCase.getBoardTitleList(title);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        //List<BoardListView> boardListViews = result.stream().map(BoardListView::new).collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponseView<>(result));
    }

    @PostMapping("/post")
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

    @PostMapping("/{board_id}/edit")
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

    @PostMapping("/{board_id}/delete")
    public ResponseEntity<ApiResponseView<SuccessView>> deletePost(@PathVariable int board_id) { //token email, db email

        boardOperationUseCase.deleteBoard(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping("/{board_id}/thumbs_up")
    public ResponseEntity<ApiResponseView<SuccessView>> increaseThumbs(@PathVariable int board_id) {

        boardOperationUseCase.increaseThumbs(board_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
