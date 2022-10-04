package com.hongik.pcrc.allinone.chat.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelCreateRequest {

    @NotNull
    @NotBlank
    private String ch_title;
}
