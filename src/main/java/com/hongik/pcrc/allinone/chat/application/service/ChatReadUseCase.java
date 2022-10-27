package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import lombok.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatReadUseCase {

    List<FindChannelResult> searchChannel(ChannelFindQuery command);
    List<FindChatListResult> enterChannel(int channel_id) throws IOException;
    List<FindChannelResult> getMyChannels();
    List<FindMyFriendResult> getMyFriendsListInChannel(int channel_id);
    List<FindChatListResult> findContentInChannel(ContentFindQuery command);

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class ChannelFindQuery {
        private SearchEnum searchEnum;
        private String title;

        public ChannelFindQuery(SearchEnum searchEnum, String title) {
            this.searchEnum = searchEnum;
            this.title = title;
        }
    }

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class ContentFindQuery {
        private int channel_id;
        private String content;

        public ContentFindQuery(int channel_id, String content) {
            this.channel_id = channel_id;
            this.content = content;
        }
    }

    @Getter
    @ToString
    @Builder
    class FindChannelResult {
        private final int channel_id;
        private final String ch_title;
        private final int number_of_users;
        private final LocalDateTime created_date;
    }

    @Getter
    @ToString
    @Builder
    class FindMyFriendResult {
        private final int friend_id;
        private final String user_email;
        private final String user_name;
    }

    @Getter
    @ToString
    @Builder
    class FindChatListResult {
        private final int chat_id;
        private final int channel_id;
        private final String user_email;
        private final String user_name;
        private final String fileName;
        private final String type;
        private final String content;
        private final LocalDateTime timestamp;
    }
}
