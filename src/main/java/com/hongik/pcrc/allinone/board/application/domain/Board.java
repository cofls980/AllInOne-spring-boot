package com.hongik.pcrc.allinone.board.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class Board {

    private final int board_id;
    private final String title;
    private final String content;
    private final String user_id;
    private final LocalDateTime b_date;
    private final int views;
}
