package com.hongik.pcrc.allinone.board.ui.requestBody;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardRequest {

    @NonNull
    private String title;
    @NonNull
    private String content;

}
