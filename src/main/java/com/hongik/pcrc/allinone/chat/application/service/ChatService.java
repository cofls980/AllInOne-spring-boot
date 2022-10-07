package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import com.hongik.pcrc.allinone.chat.application.domain.Channel;
import com.hongik.pcrc.allinone.chat.application.domain.ChannelUsers;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ChatService {

    private final ChatMapperRepository chatMapperRepository;
    private final AuthEntityRepository authEntityRepository;

    public ChatService(ChatMapperRepository chatMapperRepository, AuthEntityRepository authEntityRepository) {
        this.chatMapperRepository = chatMapperRepository;
        this.authEntityRepository = authEntityRepository;
    }

    public List<ChannelEntity> searchChannel(SearchEnum searchEnum, String ch_title) {

        if (searchEnum == SearchEnum.NOTHING) {
            return chatMapperRepository.getChannelList();
        } else {
            return chatMapperRepository.searchChannelListWithTitle(ch_title);
        }
    }

    public void createChannel(String ch_title) {

        //공백 에러 처리
        if (ch_title.contains(" ")) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        //제목 중복
        if (chatMapperRepository.isExistedTitle(ch_title)) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        var channel = Channel.builder()
                .ch_title(ch_title)
                .created_date(LocalDateTime.now())
                .build();
        chatMapperRepository.createChannel(new ChannelEntity(channel));
        String email = getUserEmail();
        var channelUsers = ChannelUsers.builder()
                .channel_id(chatMapperRepository.getChannelIdByTitle(ch_title))
                .user_email(email)
                .user_name(authEntityRepository.findByEmailResultName(email))
                .build();
        chatMapperRepository.addUserAboutChannel(new ChannelUsersEntity(channelUsers));
    }

    public List<ChatEntity> enterChannel(int channel_id) {

        String email = getUserEmail();

        if (!chatMapperRepository.isExistedUser(channel_id, email)) {
            // 1. 처음 입장한 경우
            var channelUsers = ChannelUsers.builder()
                    .channel_id(channel_id)
                    .user_email(email)
                    .user_name(authEntityRepository.findByEmailResultName(email))
                    .build();
            chatMapperRepository.addUserAboutChannel(new ChannelUsersEntity(channelUsers));
            chatMapperRepository.increaseChannelNumberOfUsers(channel_id);
        }// 2. 이미 입장한 이력이 있는 경우

        return chatMapperRepository.getRecordsInChannel(channel_id);
    }

    public void leaveChannel(int channel_id) { // 탈퇴 했을 때만 이름없음....? 괜찮은데??

        String email = getUserEmail();

        var channelEntity = chatMapperRepository.findChannelInfo(channel_id);
        if (channelEntity == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        if (channelEntity.getNumber_of_users() == 1) {
            // 마지막으로 남아있는 경우
            chatMapperRepository.deleteAllRecordsInChannel(channel_id);
            chatMapperRepository.leaveTheChannel(channel_id, email);
            chatMapperRepository.deleteChannel(channel_id);
        } else {
            // 남아있는 유저가 2명 이상인 경우
            chatMapperRepository.leaveTheChannel(channel_id, email);
            chatMapperRepository.decreaseChannelNumberOfUsers(channel_id);
        }
    }

    public List<ChannelEntity> getMyChannels() {
        return chatMapperRepository.getMyChannelList(getUserEmail());
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }

    //---------------------------------------------------------------------------
    public void clearChannel(int channel_id) {
        chatMapperRepository.deleteAllInChannel(channel_id);
    }

    public void deleteOne(int channel_id, int chat_id) {
        chatMapperRepository.deleteAllInChannelNum(channel_id, chat_id);
    }

}
