package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardEntityRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BoardService implements BoardReadUseCase, BoardOperationUseCase {

    private final BoardEntityRepository boardRepository;
    private final BoardMapperRepository boardMapperRepository;
    private final AuthEntityRepository authEntityRepository;

    public BoardService(BoardEntityRepository boardRepository, BoardMapperRepository boardMapperRepository, AuthEntityRepository authEntityRepository) {
        this.boardRepository = boardRepository;
        this.boardMapperRepository = boardMapperRepository;
        this.authEntityRepository = authEntityRepository;
    }

    @Override
    public void createBoard(BoardCreatedCommand command) {

        String userId = getUserId();

        var auth = authEntityRepository.findById(userId);

        var board = Board.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .writer(auth.get().getName())
                .writer_email(userId)
                .date(LocalDateTime.now())
                .build();

        boardMapperRepository.post(board);
    }

    @Override
    public void updateBoard(BoardUpdateCommand command) { // 존재하면 수정

        var boardEntity = boardMapperRepository.getPost(command.getId());
        if (boardEntity == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String userId = getUserId();
        if (!userId.equals(boardEntity.getWriter_email())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        var board = Board.builder()
                .id(command.getId())
                .title(command.getTitle())
                .content(command.getContent())
                .date(LocalDateTime.now())
                .build();

        boardMapperRepository.update(board);
    }

    @Override
    public void deleteBoard(int id) { // 존재하면 삭제
        var board = boardMapperRepository.getPost(id);

        if (board == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String userId = getUserId();
        if (!userId.equals(board.getWriter_email())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        boardMapperRepository.delete(id);
    }

    @Override
    public List<FindBoardResult> getBoardList() {
        var result = boardRepository.findAll();
        var boards = StreamSupport.stream(result.spliterator(), false)
                .map(BoardEntity::toBoard)
                .collect(Collectors.toList());
        if (boards.isEmpty())
            return null;
        return boards.stream().map(FindBoardResult::findByBoard).collect(Collectors.toList());
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        return id;
    }
}
