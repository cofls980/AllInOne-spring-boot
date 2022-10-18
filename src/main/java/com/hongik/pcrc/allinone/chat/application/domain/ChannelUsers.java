package com.hongik.pcrc.allinone.chat.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ChannelUsers {

    private final int channel_users_id;
    private final int channel_id;
    private final String user_email;
    private final String user_name;
}
