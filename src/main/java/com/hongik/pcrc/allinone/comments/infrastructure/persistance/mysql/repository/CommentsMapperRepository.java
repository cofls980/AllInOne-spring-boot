package com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.comments.application.domain.Comments;
import com.hongik.pcrc.allinone.comments.application.service.CommentsReadUseCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentsMapperRepository {

    public void storeComment(Comments comments);

    public void updateComment(Comments comments);

    public void deleteComment(int comment_id);

    public Comments getComment(int comment_id);

    public List<CommentsReadUseCase.FindCommentResult> getCommentsForBoard(int board_id);
}
