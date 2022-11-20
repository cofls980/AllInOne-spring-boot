package com.hongik.pcrc.allinone.comments.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.comments.application.domain.Comments;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity.CommentsEntity;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsEntityRepository;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsService implements CommentsOperationUseCase, CommentsReadUseCase {

    private final CommentsMapperRepository commentsMapperRepository;
    private final CommentsEntityRepository commentsEntityRepository;
    private final AuthEntityRepository authEntityRepository;

    public CommentsService(CommentsMapperRepository commentsMapperRepository, CommentsEntityRepository commentsEntityRepository, AuthEntityRepository authEntityRepository) {
        this.commentsMapperRepository = commentsMapperRepository;
        this.commentsEntityRepository = commentsEntityRepository;
        this.authEntityRepository = authEntityRepository;
    }

    @Override
    public void createComment(CommentCreatedCommand command) {

        //board_id 존재하는지 확인 필요?
        String userId = getUserId();
        var authEntity = authEntityRepository.findByEmail(userId);

        var comment = Comments.builder()
                .comment(command.getComment())
                .c_writer(authEntity.get().getName())
                .user_id(authEntity.get().getId().toString())
                .c_date(LocalDateTime.now())
                .board_id(command.getBoard_id())
                .build();

        commentsMapperRepository.storeComment(comment);
    }

    @Override
    public void updateComment(CommentUpdateCommand command) {

        var commentEntity = commentsMapperRepository.getComment(command.getComment_id());

        if (commentEntity == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        if (commentEntity.getBoard_id() != command.getBoard_id()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String userId = getUserId();
        var authEntity = authEntityRepository.findByEmail(userId);
        if (!authEntity.get().getId().toString().equals(commentEntity.getUser_id())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        var comment = Comments.builder()
                .comment_id(command.getComment_id())
                .comment(command.getComment())
                .c_date(LocalDateTime.now())
                .board_id(command.getBoard_id())
                .build();

        commentsMapperRepository.updateComment(comment);
    }

    @Override
    public void deleteComment(int board_id, int comment_id) {

        var comment = commentsMapperRepository.getComment(comment_id);

        if (comment == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        if (comment.getBoard_id() != board_id) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String userId = getUserId();
        var authEntity = authEntityRepository.findByEmail(userId);
        if (!authEntity.get().getId().toString().equals(comment.getUser_id())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        commentsMapperRepository.deleteComment(comment_id);
    }

    @Override
    public List<FindCommentResult> getCommentList(int board_id) {

        List<FindCommentResult> result = new ArrayList<>();

        List<CommentsEntity> comments = commentsEntityRepository.findByBoard_id(board_id);

        // 최근 순으로 정렬
        comments.sort((o1, o2) -> {
            LocalDateTime age1 = o1.getC_date();
            LocalDateTime age2 = o2.getC_date();
            return age2.compareTo(age1);
        });

        for (CommentsEntity c : comments) {
            result.add(FindCommentResult.builder()
                    .comment_id(c.getComment_id())
                    .comment(c.getComment())
                    .email(c.getUser_id().getEmail())
                    .c_writer(c.getUser_id().getName())
                    .c_date(c.getC_date())
                    .build()
            );
        }

        return result;
        /*var result = commentsMapperRepository.getCommentsForBoard(board_id);

        return result.isEmpty() ? null : result;*/
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
