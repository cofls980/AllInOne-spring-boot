package com.hongik.pcrc.allinone.board.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.board.application.domain.Board;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.BoardMapperRepository;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository.LikesViewsMapperRepository;
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
    private final AuthMapperRepository authMapperRepository;
    private final CommentsReadUseCase commentsReadUseCase;
    private final LikesViewsMapperRepository likesViewsMapperRepository;

    public BoardService(BoardMapperRepository boardMapperRepository, AuthMapperRepository authMapperRepository,
                        CommentsReadUseCase commentsReadUseCase, LikesViewsMapperRepository likesViewsMapperRepository) {
        this.boardMapperRepository = boardMapperRepository;
        this.authMapperRepository = authMapperRepository;
        this.commentsReadUseCase = commentsReadUseCase;
        this.likesViewsMapperRepository = likesViewsMapperRepository;
    }

    @Override
    public void createBoard(BoardCreatedCommand command) {

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        var board = Board.builder()
                .title(command.getTitle())
                .content(command.getContent())
                .user_id(user_id)
                .b_date(LocalDateTime.now())
                .build();

        boardMapperRepository.post(board);
    }

    @Override
    public void updateBoard(BoardUpdateCommand command) {

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);
        if (boardMapperRepository.notExistedPostWithWriter(command.getId(), user_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
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
    public void deleteBoard(int board_id) {

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);
        if (boardMapperRepository.notExistedPostWithWriter(board_id, user_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        boardMapperRepository.delete(board_id);
    }

    @Override
    public List<FindBoardResult> getBoardList(SearchEnum searchEnum, String[] query_info) {

        String user_id = authMapperRepository.getUUIDByEmail(getUserEmail());
        ArrayList<String> list = new ArrayList<>();
        String type;

        if (searchEnum == SearchEnum.NOTHING) {
            type = "n";
        } else if (searchEnum == SearchEnum.TITLE) {
            type = "t";
            Collections.addAll(list, query_info[1].split(" "));
        } else if (searchEnum == SearchEnum.WRITER) {
            type = "w";
            list.add(query_info[2]);
        } else {
            type = "a";
            Collections.addAll(list, query_info[0].split(" "));
        }

        List<HashMap<String, Object>> boards = boardMapperRepository.searchPosts(user_id, list, type);
        List<FindBoardResult> result = new ArrayList<>();

        for (HashMap<String, Object> b : boards) {
            result.add(FindBoardResult.findByBoard(b));
        }
        return result;
    }

    @Override
    public FindOneBoardResult getOneBoard(int board_id) {

        if (boardMapperRepository.notExistedPost(board_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        System.out.println(email);
        if (email != null && !likesViewsMapperRepository.checkView(user_id, board_id)) {
            System.out.println("INSIDE=================");
            likesViewsMapperRepository.createView(user_id, board_id);
        }

        HashMap<String, Object> result = boardMapperRepository.getOnePost(board_id, user_id);

        return FindOneBoardResult.findByOneBoard(result, commentsReadUseCase.getCommentList(board_id));
    }

    @Override
    public void increaseLikes(int board_id) {

        if (boardMapperRepository.notExistedPost(board_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);
        if (likesViewsMapperRepository.isUserLikes(user_id, board_id) != 0) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        likesViewsMapperRepository.createLikes(user_id, board_id);
    }

    @Override
    public void deleteLikes(int board_id) {

        if (boardMapperRepository.notExistedPost(board_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);
        if (likesViewsMapperRepository.isUserLikes(user_id, board_id) == 0) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        likesViewsMapperRepository.deleteLikes(user_id, board_id);
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
