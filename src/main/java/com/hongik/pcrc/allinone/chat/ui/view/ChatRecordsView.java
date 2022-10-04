package com.hongik.pcrc.allinone.chat.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRecordsView {
    private final List<ChannelUsersEntity> users;
    private final List<ChatEntity> records;

    public ChatRecordsView(List<ChannelUsersEntity> users, List<ChatEntity> records) {
        this.users = users;
        this.records = records;
    }
}
