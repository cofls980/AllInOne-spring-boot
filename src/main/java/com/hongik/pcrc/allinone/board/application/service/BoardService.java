package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService implements BoardReadUseCase, BoardOperationUseCase {

    private final BoardMapperRepository boardMapperRepository;
    private final AuthEntityRepository authEntityRepository;
    private final CommentsMapperRepository commentsMapperRepository;

    public BoardService(BoardMapperRepository boardMapperRepository,
                        AuthEntityRepository authEntityRepository,
                        CommentsMapperRepository commentsMapperRepository) {
        this.boardMapperRepository = boardMapperRepository;
        this.authEntityRepository = authEntityRepository;
        this.commentsMapperRepository = commentsMapperRepository;
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
    public void updateBoard(BoardUpdateCommand command) {

        var boardEntity = boardMapperRepository.havePost(command.getId());
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
        var board = boardMapperRepository.havePost(id);

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
    public List<FindBoardResult> getBoardList(String b_writer, String title) {

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

        String user_id = getUserId();
        boolean click = user_id!=null && boardMapperRepository.isUserLikes(getMap(user_id, board_id)) != 0;

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
                .click_likes(click)
                .build();
        return result;
    }

    @Override
    public void increaseLikes(int board_id) {

        String user_id = getUserId();

        if (boardMapperRepository.isUserLikes(getMap(user_id, board_id)) != 0) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        boardMapperRepository.createLikes(getMap(user_id, board_id));
    }

    @Override
    public void deleteLikes(int board_id) {

        String user_id = getUserId();

        if (boardMapperRepository.isUserLikes(getMap(user_id, board_id)) == 0) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        boardMapperRepository.deleteLikes(getMap(user_id, board_id));
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        return id;
    }

    public Map<String, Object> getMap(String user_id, int board_id) {

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("board_id", board_id);

        return map;
    }
}
