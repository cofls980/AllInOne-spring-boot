package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.chat.application.service.ChatService;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService implements AuthOperationUseCase, AuthReadUseCase {

    private final AuthEntityRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapperRepository authMapperRepository;
    private final ChatMapperRepository chatMapperRepository;
    private final ChatService chatService;

    @Autowired
    public AuthService(AuthEntityRepository authRepository, PasswordEncoder passwordEncoder, AuthMapperRepository authMapperRepository, ChatMapperRepository chatMapperRepository, ChatService chatService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.authMapperRepository = authMapperRepository;
        this.chatMapperRepository = chatMapperRepository;
        this.chatService = chatService;
    }

    @Override
    public void createAuth(AuthCreatedCommand command) {

        var query = authMapperRepository.existsByEmail(command.getEmail());
        if (query) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        var auth = Auth.builder()
                .email(command.getEmail())
                .password(encodePassword)
                .name(command.getName())
                .birth(command.getBirth())
                .gender(command.getGender())
                .phone_number(command.getPhone_number())
                .build();

        var authEntity = new AuthEntity(auth);

        authRepository.save(authEntity);
    }

    @Override
    public void updateAuth(AuthUpdateCommand command) {

        var auth = authRepository.findByEmail(command.getEmail());

        if (auth.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        if (passwordEncoder.matches(command.getPassword(), auth.get().getPassword())) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        authMapperRepository.updatePwd(auth.get().getId().toString(), encodePassword);
    }

    @Override
    public void deleteAuth() {

        String email = getUserEmail();

        if (!authMapperRepository.existsByEmail(email)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        //-------------chat-------------//
        List<HashMap<String, Object>> channelList = chatMapperRepository.getChannelsOfUser(email);
        for (HashMap<String, Object> h : channelList) {
            chatService.leaveChannel((int) h.get("channel_id"));
        }
        //대화 기록 수정
        chatMapperRepository.changeUserRecordsNon(email);

        authMapperRepository.deleteByEmail(email);
    }

    @Override
    public FindAuthResult getAuth(AuthFindQuery query) { // login

        var auth = authRepository.findByEmail(query.getEmail());

        if (auth.isEmpty() || !passwordEncoder.matches(query.getPassword(), auth.get().getPassword())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        return FindAuthResult.findByAuth(auth.get().toAuth());
    }

    @Override
    public List<FindMyFriendResult> getMyFriendList() {

        String email = getUserEmail();

        //get UUID
        String uuid = authMapperRepository.getUUIDByEmail(email);
        //get friend list
        List<HashMap<String, Object>> friendList = authMapperRepository.getFriendList(uuid);
        List<FindMyFriendResult> result = new ArrayList<>();
        for (HashMap<String, Object> h : friendList) {
            // get user's info
            var friend = authMapperRepository.getFriendInfo(h.get("user2").toString());
            if (friend == null) // 탈퇴한 회원인 경우
            {
                result.add(FindMyFriendResult.builder()
                        .friend_id(Integer.parseInt(h.get("friend_id").toString()))
                        .user_email("null")
                        .user_name("알 수 없음")
                        .build());
            } else {
                result.add(FindMyFriendResult.builder()
                        .friend_id(Integer.parseInt(h.get("friend_id").toString()))
                        .user_email(friend.getEmail())
                        .user_name(friend.getName())
                        .build());
            }
        }
        return result;
    }

    @Override
    public void addFriend(FriendCreatedCommand command) {
        String email = getUserEmail();

        //get UUID
        String user1 = authMapperRepository.getUUIDByEmail(email);
        //get friend's uuid from command
        //친구가 있는지 확인 없으면 non_found
        String user2 = authMapperRepository.getFriendUUID(command.getEmail(), command.getName());
        if (user2 == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        //insert uuid pair into test_user_friend
        //이미 있는지 확인 있으면 conflict
        if (authMapperRepository.existedComb(user1, user2) || user1.equals(user2)) {
            throw new AllInOneException(MessageType.CONFLICT);
        }
        authMapperRepository.addFriend(user1, user2);
    }

    @Override
    public void deleteFriend(int friend_id) {
        authMapperRepository.deleteFriend(friend_id);
    }

    @Override
    public void updateRefreshToken(UUID id, String refresh_token) {

        authMapperRepository.updateRefreshToken(id.toString(), refresh_token);
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
