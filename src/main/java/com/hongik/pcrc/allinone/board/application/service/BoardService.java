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
                .thumbs_up(board.getThumbs_up() + 1)
                .build();

        boardMapperRepository.updateThumbs(result);
    }

    @Override
    public List<FindBoardResult> getBoardList() { //수정
        var result = boardRepository.findAll();//mapper 사용으로 변경

        var boards = StreamSupport.stream(result.spliterator(), false)
                .map(BoardEntity::toBoard)
                .collect(Collectors.toList());
        if (boards.isEmpty())
            return null;
        return boards.stream().map(FindBoardResult::findByBoard).collect(Collectors.toList());
    }

    @Override
    public FindOneBoardResult getOneBoard(int board_id) {

        var board = boardMapperRepository.getPost(board_id);

        if (board == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        var comments = commentsMapperRepository.getCommentsForBoard(board_id);

        if (comments.isEmpty())
                return null;

        var result = FindOneBoardResult.builder()
                .board_id(board_id)
                .title(board.getTitle())
                .content(board.getContent())
                .b_writer(board.getB_writer())
                .b_date(board.getB_date())
                .thumbs_up(board.getThumbs_up())
                .commentList(comments)
                .build();
        return result;
    }

    @Override
    public List<FindBoardResult> getBoardWriterList(String b_writer) {

        var result = boardMapperRepository.searchWriter(b_writer);

        if (result.isEmpty())
            return null;

        return result;
    }

    @Override
    public List<FindBoardResult> getBoardTitleList(String title) {

        var result = boardMapperRepository.searchTitle(title);

        if (result.isEmpty())
            return null;

        return result;
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        return id;
    }
}
