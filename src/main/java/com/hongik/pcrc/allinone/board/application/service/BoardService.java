package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardEntityRepository;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsMapperRepository;
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
    private final CommentsMapperRepository commentsMapperRepository;
    private final CommentsReadUseCase commentsReadUseCase;

    public BoardService(BoardEntityRepository boardRepository, BoardMapperRepository boardMapperRepository,
                        AuthEntityRepository authEntityRepository, CommentsMapperRepository commentsMapperRepository, CommentsReadUseCase commentsReadUseCase) {
        this.boardRepository = boardRepository;
        this.boardMapperRepository = boardMapperRepository;
        this.authEntityRepository = authEntityRepository;
        this.commentsMapperRepository = commentsMapperRepository;
        this.commentsReadUseCase = commentsReadUseCase;
    }

    @Override
    public void createBoard(BoardCreatedCommand command) {

        String userId = getUserId();

        var auth = authEntityRepository.findById(userId);

        var board = Board.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .b_writer(auth.get().getName())
                .writer_email(userId)
                .b_date(LocalDateTime.now())
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
                .board_id(command.getId())
                .title(command.getTitle())
                .content(command.getContent())
                .b_date(LocalDateTime.now())
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
    public void increaseThumbs(int id) { //일단은 사용자 정보 저장되지 않는 좋아요

        var board = boardMapperRepository.getPost(id);

        if (board == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        var result = Board.builder()
                .board_id(id)
                .likes(board.getLikes() + 1)
                .build();

        boardMapperRepository.updateLike(result);
    }

    @Override
    public List<FindBoardResult> getBoardList(String b_writer, String title) { //수정

        List<FindBoardResult> result = null;

        if (b_writer == null && title == null) {
            result = boardMapperRepository.getList();
        } else if (title == null) {
            result = boardMapperRepository.searchWriter(b_writer);
        } else if (b_writer == null) {
            result = boardMapperRepository.searchTitle(title);
        } else {
            if (b_writer.equals(title))
                result = boardMapperRepository.searchBothWriterTitle(b_writer);
        }

        if (result == null || result.isEmpty()) {
            return null;
        }

        return result;
    }

    @Override
    public FindOneBoardResult getOneBoard(int board_id) {

        var board = boardMapperRepository.getPost(board_id);

        if (board == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        var comments = commentsMapperRepository.getCommentsForBoard(board_id);

        if (comments.isEmpty())
            comments = null;

        var result = FindOneBoardResult.builder()
                .board_id(board_id)
                .title(board.getTitle())
                .content(board.getContent())
                .b_writer(board.getB_writer())
                .b_date(board.getB_date())
                .likes(board.getLikes())
                .commentList(comments)
                .build();
        return result;
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        return id;
    }
}
