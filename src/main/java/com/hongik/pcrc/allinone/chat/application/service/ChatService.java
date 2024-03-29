package com.hongik.pcrc.allinone.chat.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import com.hongik.pcrc.allinone.chat.application.domain.Channel;
import com.hongik.pcrc.allinone.chat.application.domain.ChannelUsers;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService implements ChatOperationUseCase, ChatReadUseCase {

    private final ChatMapperRepository chatMapperRepository;
    private final AuthEntityRepository authEntityRepository;
    private final AuthMapperRepository authMapperRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.bucket.name}")
    private String S3Bucket;

    public ChatService(ChatMapperRepository chatMapperRepository,
                       AuthEntityRepository authEntityRepository,
                       AuthMapperRepository authMapperRepository,
                       AmazonS3Client amazonS3Client) {
        this.chatMapperRepository = chatMapperRepository;
        this.authEntityRepository = authEntityRepository;
        this.authMapperRepository = authMapperRepository;
        this.amazonS3Client = amazonS3Client;
    }

    public List<FindChannelResult> searchChannel(ChannelFindQuery command) {

        List<HashMap<String, Object>> list;
        ArrayList<String> strs = new ArrayList<>();
        if (command.getSearchEnum() == SearchEnum.TITLE) {
            Collections.addAll(strs, command.getTitle().split(" "));
        }

        list =  chatMapperRepository.getChannelList(strs);
        List<FindChannelResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : list) {
            result.add(FindChannelResult.builder()
                    .channel_id(Integer.parseInt(h.get("channel_id").toString()))
                    .ch_title(h.get("ch_title").toString())
                    .number_of_users(Integer.parseInt(h.get("number_of_users").toString()))
                    .created_date(LocalDateTime.parse(h.get("created_date").toString()))
                    .build());
        }

        return result;
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

    public List<FindChatListResult> enterChannel(int channel_id) throws IOException {

        String email = getUserEmail();

        //is channel existed
        if (chatMapperRepository.notExistedChannel(channel_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

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

        var list = chatMapperRepository.getRecordsInChannel(channel_id);
        List<ChatReadUseCase.FindChatListResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : list) {
            var content = h.get("content").toString();
            String fileName = null;
            if (h.get("type").toString().equals("image/png") || h.get("type").toString().equals("image/jpeg")) { // 사진이면 파일명, 확장자 필요
                String tmp = content.substring(0, content.lastIndexOf("."));
                fileName = tmp.substring(tmp.lastIndexOf("/") + 1);
                S3Object object = amazonS3Client.getObject(new GetObjectRequest(S3Bucket, content));
                S3ObjectInputStream objectInputStream = object.getObjectContent();
                byte[] bytes = IOUtils.toByteArray(objectInputStream);
                content = Base64.getEncoder().encodeToString(bytes);
            }
            result.add(FindChatListResult.builder()
                    .chat_id(Integer.parseInt(h.get("chat_id").toString()))
                    .channel_id(Integer.parseInt(h.get("channel_id").toString()))
                    .ch_title(h.get("ch_title").toString())
                    .user_email(h.get("user_email").toString())
                    .user_name(h.get("user_name").toString())
                    .content(content)
                    .fileName(fileName)
                    .type(h.get("type").toString())
                    .timestamp(LocalDateTime.parse(h.get("timestamp").toString()))
                    .build());
        }
        return result;
    }

    public void leaveChannel(int channel_id) {

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

    public List<FindChannelResult> getMyChannels() {

        List<HashMap<String, Object>> list = chatMapperRepository.getMyChannelList(getUserEmail());
        List<FindChannelResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : list) {
            result.add(FindChannelResult.builder()
                    .channel_id(Integer.parseInt(h.get("channel_id").toString()))
                    .ch_title(h.get("ch_title").toString())
                    .number_of_users(Integer.parseInt(h.get("number_of_users").toString()))
                    .created_date(LocalDateTime.parse(h.get("created_date").toString()))
                    .build());
        }
        return result;
    }

    public List<ChatReadUseCase.FindMyFriendResult> getMyFriendsListInChannel(int channel_id) {

        //is channel existed
        if (chatMapperRepository.notExistedChannel(channel_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();

        //get UUID
        String uuid = authMapperRepository.getUUIDByEmail(email);
        //get friend list
        List<HashMap<String, Object>> friendList = authMapperRepository.getFriendList(uuid);
        List<ChatReadUseCase.FindMyFriendResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : friendList) {
            // get user's info
            var friend = authMapperRepository.getFriendInfo(h.get("user2").toString());
            if (friend != null) // 탈퇴한 회원인 경우는 제외하고 출력
            {
                if (!chatMapperRepository.isExistedUser(channel_id, friend.getEmail())) {// 이미 채널에 있는 회원일 경우 제외
                    result.add(ChatReadUseCase.FindMyFriendResult.builder()
                            .friend_id(Integer.parseInt(h.get("friend_id").toString()))
                            .user_email(friend.getEmail())
                            .user_name(friend.getName())
                            .build());
                }
            }
        }
        return result;
    }

    public void inviteMyFriend(InviteCommand command) {
        // channel_id가 있는지 확인 후 친구 초대
        if (chatMapperRepository.notExistedChannel(command.getChannel_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        if (chatMapperRepository.isExistedUser(command.getChannel_id(), command.getUser_email())) {
            throw new AllInOneException(MessageType.CONFLICT);
        }
        chatMapperRepository.addUserAboutChannel(new ChannelUsersEntity(
                ChannelUsers.builder()
                .channel_id(command.getChannel_id())
                .user_email(command.getUser_email())
                .user_name(command.getUser_name())
                .build()));
        chatMapperRepository.increaseChannelNumberOfUsers(command.getChannel_id());
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }

    @Override
    public List<FindChatListResult> findContentInChannel(ContentFindQuery command) {
        var list = chatMapperRepository.findContentInChannel(command.getChannel_id(), command.getContent());
        List<ChatReadUseCase.FindChatListResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : list) {
            if (h.get("type").toString().equals("TEXT")) {
                result.add(FindChatListResult.builder()
                        .chat_id(Integer.parseInt(h.get("chat_id").toString()))
                        .channel_id(Integer.parseInt(h.get("channel_id").toString()))
                        .user_email(h.get("user_email").toString())
                        .user_name(h.get("user_name").toString())
                        .content(h.get("content").toString())
                        .type(h.get("type").toString())
                        .timestamp(LocalDateTime.parse(h.get("timestamp").toString()))
                        .build());
            }
        }
        return result;
    }

    //---------------------------------------------------------------------------
    public void deleteOne(int channel_id, int chat_id) {
        chatMapperRepository.deleteAllInChannelNum(channel_id, chat_id);
    }

}
