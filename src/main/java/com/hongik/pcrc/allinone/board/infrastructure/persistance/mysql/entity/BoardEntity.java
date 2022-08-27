package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.board.application.domain.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "board")
public class BoardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int board_id;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false, length = 2000)
    private String content;
    @Column(nullable = false)
    private String b_writer;
    @Column(nullable = false)
    private String writer_email;
    @Column(nullable = false)
    private LocalDateTime b_date;
    @Column(nullable = false)
    @ColumnDefault("0")
    private int thumbs_up;

    public Board toBoard() {
        return Board.builder()
                .board_id(this.board_id)
                .title(this.title)
                .content(this.content)
                .b_writer(this.b_writer)
                .writer_email(this.writer_email)
                .b_date(this.b_date)
                .thumbs_up(this.thumbs_up)
                .build();
    }

    public BoardEntity(Board board) {
        this.board_id = board.getBoard_id();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.b_writer = board.getB_writer();
        this.writer_email = board.getWriter_email();
        this.b_date = board.getB_date();
        this.thumbs_up = board.getThumbs_up();
    }
}
