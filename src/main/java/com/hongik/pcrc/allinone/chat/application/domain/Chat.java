package com.hongik.pcrc.allinone.chat.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class Chat {

    private final int chat_id;
    private final int channel_id;
    private final String user_email;
    private final String user_name;
    private final String content;
    private final String type;
    private final LocalDateTime timestamp;
}
