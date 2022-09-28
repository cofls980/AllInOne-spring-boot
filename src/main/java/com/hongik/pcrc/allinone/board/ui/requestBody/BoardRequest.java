package com.hongik.pcrc.allinone.board.ui.requestBody;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardRequest {

    @NotNull @NotBlank
    private String title;
    @NotNull @NotBlank
    private String content;

}
