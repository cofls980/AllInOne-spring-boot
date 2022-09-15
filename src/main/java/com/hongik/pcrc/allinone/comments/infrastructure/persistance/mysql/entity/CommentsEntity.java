package com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(nullable = false)
    private LocalDateTime c_date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AuthEntity user_id;
    @ManyToOne
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoardEntity board_id;

    /*public Comments toComments() {
        return Comments.builder()
                .comment_id(this.comment_id)
                .comment(this.comment)
                .c_writer(this.c_writer)
                .user_id(this.user_id)
                .c_date(this.c_date)
                .board_id(this.board_id)
                .build();
    }

    public CommentsEntity(Comments comments) {
        this.comment_id = comments.getComment_id();
        this.comment = comments.getComment();
        this.c_writer = comments.getC_writer();
        this.user_id = comments.getUser_id();
        this.c_date = comments.getC_date();
        this.board_id = comments.getBoard_id();
    }*/
}
