package com.hongik.pcrc.allinone.board.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
@Builder
public class Board {

    private final int id;
    private final String title;
    private final String contents;
    private final String writer;
    private final String writer_email;
    private final LocalDateTime date;
}
