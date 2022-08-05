package com.hongik.pcrc.allinone.board.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.BoardOperationUseCase;
import com.hongik.pcrc.allinone.board.application.service.BoardReadUseCase;
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

    @PostMapping("/post")
    public ResponseEntity<ApiResponseView<SuccessView>> createPost(@RequestBody BoardRequest request) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = BoardOperationUseCase.BoardCreatedCommand.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .writer(request.getWriter())
                .writer_email(request.getWriterEmail())
                .build();

        boardOperationUseCase.createBoard(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PostMapping("/{board_id}/edit")
    public ResponseEntity<ApiResponseView<SuccessView>> editPost(@RequestBody BoardRequest request, @PathVariable int board_id) { //token email, request email, db email

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        if (!id.equals(request.getWriterEmail())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        var command = BoardOperationUseCase.BoardUpdateCommand.builder()
                .id(board_id)
                .title(request.getTitle())
                .contents(request.getContents())
                .writer(request.getWriter())
                .writer_email(request.getWriterEmail())
                .build();

        boardOperationUseCase.updateBoard(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PostMapping("/{board_id}/delete")
    public ResponseEntity<ApiResponseView<SuccessView>> deletePost(@PathVariable int board_id) { //token email, db email

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        boardOperationUseCase.deleteBoard(board_id, id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
