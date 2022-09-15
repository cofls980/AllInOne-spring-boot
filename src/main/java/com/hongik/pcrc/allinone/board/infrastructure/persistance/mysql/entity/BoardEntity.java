package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class BoardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int board_id;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false, length = 2000)
    private String content;
    @Column(nullable = false)
    private LocalDateTime b_date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AuthEntity user_id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board_id",
            cascade = CascadeType.REMOVE)
    private List<LikesEntity> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board_id",
            cascade = CascadeType.REMOVE)
    private List<ViewsEntity> views;

    /*public Board toBoard() {
        return Board.builder()
                .board_id(this.board_id)
                .title(this.title)
                .content(this.content)
                .b_writer(this.b_writer)
                .user_id(this.user_id)
                .b_date(this.b_date)
                .views(this.views)
                .build();
    }

    public BoardEntity(Board board) {
        this.board_id = board.getBoard_id();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.b_writer = board.getB_writer();
        this.user_id = board.getUser_id();
        this.b_date = board.getB_date();
        //this.views = board.getViews();
    }*/
}
