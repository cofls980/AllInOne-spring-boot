package com.hongik.pcrc.allinone.chat.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ChatOperationUseCase {
    void createChannel(String ch_title);
    void leaveChannel(int channel_id);
    void inviteMyFriend(InviteCommand command);
    void deleteOne(int channel_id, int chat_id);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class InviteCommand {
        private final int channel_id;
        private final String user_email;
        private final String user_name;
    }
}
