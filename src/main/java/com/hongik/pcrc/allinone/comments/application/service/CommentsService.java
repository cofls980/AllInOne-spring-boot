package com.hongik.pcrc.allinone.comments.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.comments.application.domain.Comments;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsEntityRepository;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository.CommentsMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentsService implements CommentsOperationUseCase, CommentsReadUseCase {

    private final CommentsEntityRepository commentsEntityRepository;
    private final CommentsMapperRepository commentsMapperRepository;
    private final AuthEntityRepository authEntityRepository;

    public CommentsService(CommentsEntityRepository commentsEntityRepository, CommentsMapperRepository commentsMapperRepository, AuthEntityRepository authEntityRepository) {
        this.commentsEntityRepository = commentsEntityRepository;
        this.commentsMapperRepository = commentsMapperRepository;
        this.authEntityRepository = authEntityRepository;
    }

    @Override
    public void createComment(CommentCreatedCommand command) {

        //board_id 존재하는지 확인 필요?
        String userId = getUserId();

        var auth = authEntityRepository.findById(userId);

        var comment = Comments.builder()
                .comment(command.getComment())
                .c_writer(auth.get().getName())
                .writer_email(userId)
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
        if (!userId.equals(commentEntity.getWriter_email())) {
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
        if (!userId.equals(comment.getWriter_email())) {
            throw new AllInOneException(MessageType.FORBIDDEN);
        }

        commentsMapperRepository.deleteComment(comment_id);
    }

    @Override
    public List<FindCommentResult> getCommentList() {
        return null;
    }

    public String getUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String id = userDetails.getUsername();

        return id;
    }
}
