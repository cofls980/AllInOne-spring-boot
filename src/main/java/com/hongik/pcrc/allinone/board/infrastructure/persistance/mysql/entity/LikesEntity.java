package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class LikesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likes_id;
    @Column(nullable = false)
    private int board_id;
    @Column(nullable = false, length = 50)
    private String user_id;

}
