package com.hongik.pcrc.allinone.chat.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatUserListView {

    private final List<ChannelUsersEntity> users;

    public ChatUserListView(List<ChannelUsersEntity> users) {
        this.users = users;
    }
}
