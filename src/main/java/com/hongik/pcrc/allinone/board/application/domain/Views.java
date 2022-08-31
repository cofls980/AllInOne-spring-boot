package com.hongik.pcrc.allinone.board.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Views {

    private final int view_id;
    private final int board_id;
    private final String user_id;

}
