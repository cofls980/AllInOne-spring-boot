package com.hongik.pcrc.allinone.board.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder
public class Board {

    private final int board_id;
    private final String title;
    private final String content;
    private final String b_writer;
    private final String writer_email;
    private final LocalDateTime b_date;
    private final int views;
}
