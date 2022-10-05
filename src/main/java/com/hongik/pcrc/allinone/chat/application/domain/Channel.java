package com.hongik.pcrc.allinone.chat.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class Channel {

    private final int channel_id;
    private final String ch_title;
    private final int number_of_users;
    private final LocalDateTime created_date;
}
