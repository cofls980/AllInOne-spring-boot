package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.LikesEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardEntityRepository;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.LikesViewsMapperRepository;
import com.hongik.pcrc.allinone.board.ui.requestBody.BoardViewsRequest;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BoardService implements BoardReadUseCase, BoardOperationUseCase {

    private final BoardMapperRepository boardMapperRepository;
    private final AuthEntityRepository authEntityRepository;
    private final CommentsReadUseCase commentsReadUseCase;
    private final BoardEntityRepository boardEntityRepository;
    private final LikesViewsMapperRepository likesViewsMapperRepository;

    public BoardService(BoardMapperRepository boardMapperRepository,
                        AuthEntityRepository authEntityRepository,
                        CommentsReadUseCase commentsReadUseCase, BoardEntityRepository boardEntityRepository, LikesViewsMapperRepository likesViewsMapperRepository) {
        this.boardMapperRepository = boardMapperRepository;
        this.authEntityRepository = authEntityRepository;
        this.commentsReadUseCase = commentsReadUseCase;
        this.boardEntityRepository = boardEntityRepository;
        this.likesViewsMapperRepository = likesViewsMapperRepository;
    }

    @Override
    public void createBoard(BoardCreatedCommand command) {

        String email = getUserEmail();

        var authEntity = authEntityRepository.findByEmail(email);

        var board = Board.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .user_id(authEntity.get().getId().toString())
                .b_date(LocalDateTime.now())
                .build();

        boardMapperRepository.post(board);
    }

    @Override
    public void updateBoard(BoardUpdateCommand command) {

        var result = boardEntityRepository.findById(command.getId());
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        if (!email.equals(result.get().getUser_id().getEmail())) {
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
    public void deleteBoard(int id) {

        var result = boardEntityRepository.findById(id);
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        if (!email.equals(result.get().getUser_id().getEmail())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        boardMapperRepository.delete(id);
    }

    @Override
    public List<FindBoardResult> getBoardList(String b_writer, String title) { //수정

        List<FindBoardResult> result = new ArrayList<>();

        var boards = boardEntityRepository.findAll();

        for (BoardEntity b : boards) {
            result.add(FindBoardResult.builder()
                    .board_id(b.getBoard_id())
                    .title(b.getTitle())
                    .b_writer(b.getUser_id().getName())
                    .b_date(b.getB_date())
                    .likes(b.getLikes().size())
                    .click_likes(isLikeClicked(b.getLikes()))
                    .views(b.getViews().size())
                    .build()
            );
        }
        return result;
    }

    @Override
    public FindOneBoardResult getOneBoard(int board_id) {

        var result = boardEntityRepository.findById(board_id);
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return FindOneBoardResult.builder()
                .board_id(board_id)
                .title(result.get().getTitle())
                .content(result.get().getContent())
                .b_writer(result.get().getUser_id().getName())
                .b_date(result.get().getB_date())
                .likes(result.get().getLikes().size())
                .commentList(commentsReadUseCase.getCommentList(board_id))
                .click_likes(isLikeClicked(result.get().getLikes()))
                .views(result.get().getViews().size())
                .build();
    }

    @Override
    public void increaseLikes(int board_id) {

        String email = getUserEmail();
        var authEntity = authEntityRepository.findByEmail(email);
        if (likesViewsMapperRepository.isUserLikes(authEntity.get().getId().toString(), board_id) != 0) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        likesViewsMapperRepository.createLikes(authEntity.get().getId().toString(), board_id);
    }

    @Override
    public void deleteLikes(int board_id) {

        String email = getUserEmail();
        var authEntity = authEntityRepository.findByEmail(email);
        if (likesViewsMapperRepository.isUserLikes(authEntity.get().getId().toString(), board_id) == 0) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        likesViewsMapperRepository.deleteLikes(authEntity.get().getId().toString(), board_id);
    }

    @Override
    public void updateViews(List<BoardViewsRequest> requestList) {

        String email = getUserEmail();
        var authEntity = authEntityRepository.findByEmail(email);
        for (BoardViewsRequest data : requestList) {
            if (!boardEntityRepository.existsById(data.getBoard_id())) continue;
            var check = likesViewsMapperRepository.checkView(authEntity.get().getId().toString(), data.getBoard_id());
            if (!check) {
                likesViewsMapperRepository.createView(authEntity.get().getId().toString(), data.getBoard_id());
            }
        }
    }

    public boolean isLikeClicked(List<LikesEntity> list) {

        boolean click = false;

        String email = getUserEmail();

        if (email != null && !list.isEmpty()) {
            for (LikesEntity l : list) {
                if (l.getUser_id().getEmail().equals(email)) {
                    click = true;
                    break;
                }
            }
        }

        return click;
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
