package com.hongik.pcrc.allinone.board.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Likes {

    private final int id;
    private final int board_id;
    private final String user_id;

}
