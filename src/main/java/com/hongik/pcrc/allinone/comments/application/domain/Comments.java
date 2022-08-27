package com.hongik.pcrc.allinone.comments.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class Comments {

    private final int comment_id;
    private final String c_writer;
    private final String writer_email;
    private final String comment;
    private final LocalDateTime c_date;
    private final int board_id;
}
