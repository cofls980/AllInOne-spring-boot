package com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.comments.application.domain.Comments;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "comments")
public class CommentsEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_id;
    @Column(nullable = false, length = 500)
    private String comment;
    @Column(nullable = false)
    private String c_writer;
    @Column(nullable = false, length = 50)
    private String writer_email;
    @Column(nullable = false)
    private LocalDateTime c_date;
    @Column(nullable = false)
    private int board_id;

    public Comments toComments() {
        return Comments.builder()
                .comment_id(this.comment_id)
                .comment(this.comment)
                .c_writer(this.c_writer)
                .writer_email(this.writer_email)
                .c_date(this.c_date)
                .board_id(this.board_id)
                .build();
    }

    public CommentsEntity(Comments comments) {
        this.comment_id = comments.getComment_id();
        this.comment = comments.getComment();
        this.c_writer = comments.getC_writer();
        this.writer_email = comments.getWriter_email();
        this.c_date = comments.getC_date();
        this.board_id = comments.getBoard_id();
    }
}
