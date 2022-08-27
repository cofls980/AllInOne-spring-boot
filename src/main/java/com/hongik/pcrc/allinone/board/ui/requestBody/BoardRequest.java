package com.hongik.pcrc.allinone.board.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardRequest {

    private String title;
    private String content;

}
