package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.board.application.domain.Board;
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
@Table(name = "board")
public class BoardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String contents;
    @Column(nullable = false)
    private String writer;
    @Column(nullable = false)
    private String writer_email;
    @Column(nullable = false)
    private LocalDateTime date;

    public Board toBoard() {
        return Board.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .writer(this.writer)
                .writer_email(this.writer_email)
                .date(this.date)
                .build();
    }

    public BoardEntity(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.writer = board.getWriter();
        this.writer_email = board.getWriter_email();
        this.date = board.getDate();
    }
}
