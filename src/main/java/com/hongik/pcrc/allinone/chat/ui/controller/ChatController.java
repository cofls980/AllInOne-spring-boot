package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import com.hongik.pcrc.allinone.chat.application.service.ChatOperationUseCase;
import com.hongik.pcrc.allinone.chat.application.service.ChatReadUseCase;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChannelCreateRequest;
import com.hongik.pcrc.allinone.chat.ui.requestBody.InviteFriendRequest;
import com.hongik.pcrc.allinone.chat.ui.view.ChatRecordsView;
import com.hongik.pcrc.allinone.chat.ui.view.ChatUserListView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/v2/chat")
@Api(tags = {"Chat API"})
public class ChatController {

    private final ChatMapperRepository chatMapperRepository;
    private final ChatOperationUseCase chatOperationUseCase;
    private final ChatReadUseCase chatReadUseCase;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatController(ChatMapperRepository chatMapperRepository,
                          ChatOperationUseCase chatOperationUseCase, ChatReadUseCase chatReadUseCase) {
        this.chatMapperRepository = chatMapperRepository;
        this.chatOperationUseCase = chatOperationUseCase;
        this.chatReadUseCase = chatReadUseCase;
    }

    @GetMapping("")
    @ApiOperation(value = "채널 목록 (+검색)")
    public ResponseEntity<List<ChatReadUseCase.FindChannelResult>> searchChannel(@RequestParam(value = "title", required = false) String title) {

        SearchEnum searchEnum;

        if (title == null || title.isEmpty()){
            logger.info("전체 채널 목록");
            searchEnum = SearchEnum.NOTHING;
        } else {
            logger.info("채널 검색");
            searchEnum = SearchEnum.TITLE;
        }

        ChatReadUseCase.ChannelFindQuery command = new ChatReadUseCase.ChannelFindQuery(searchEnum, title);
        var result = chatReadUseCase.searchChannel(command);
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    @ApiOperation(value = "채널 생성")
    public ResponseEntity<ApiResponseView<SuccessView>> createChannel(@Valid @RequestBody ChannelCreateRequest request) {

        logger.info("채널 생성");

        chatOperationUseCase.createChannel(request.getCh_title());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping("/{channel_id}")
    @ApiOperation(value = "대화 목록")
    public ResponseEntity<ApiResponseView<ChatRecordsView>> enterChannel(@PathVariable int channel_id) {// 채팅방 입장

        logger.info("대화 목록");

        var result =  chatReadUseCase.enterChannel(channel_id);

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new ChatRecordsView(result)));
    }

    @GetMapping("/{channel_id}/users")
    @ApiOperation(value = "채팅방 유저 리스트")
    public ResponseEntity<ApiResponseView<ChatUserListView>> getUserListInChannel(@PathVariable int channel_id) {

        logger.info("채팅방 유저 리스트");

        var result = chatMapperRepository.getUsersInChannel(channel_id);

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new ChatUserListView(result)));
    }

    @GetMapping("/my")
    @ApiOperation(value = "내가 등록한 채널 목록")
    public ResponseEntity<List<ChatReadUseCase.FindChannelResult>> getMyChannels() {

        logger.info("내가 등록한 채널 목록");

        var result = chatReadUseCase.getMyChannels();
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{channel_id}")
    @ApiOperation(value = "채팅방 나가기 및 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> leaveChannel(@PathVariable int channel_id) {
        // 탈퇴한 사람의 경우 "이름없음"으로 처리

        logger.info("채팅방 나가기 및 삭제");

        chatOperationUseCase.leaveChannel(channel_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping("/{channel_id}/friends")
    @ApiOperation(value = "채팅방에 초대할 수 있는 친구 리스트")
    public ResponseEntity<List<ChatReadUseCase.FindMyFriendResult>> getMyFriendsListInChannel(@PathVariable int channel_id) {
        logger.info("채팅방에 초대할 수 있는 친구 리스트");

        var result = chatReadUseCase.getMyFriendsListInChannel(channel_id);
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{channel_id}/invite")
    @ApiOperation(value = "채팅방에 친구 초대")
    public ResponseEntity<ApiResponseView<SuccessView>> inviteMyFriend(@PathVariable int channel_id,
                                                                       @Valid @RequestBody InviteFriendRequest request) {

        logger.info("채팅방에 친구 초대");

        var command = ChatOperationUseCase.InviteCommand.builder()
                .channel_id(channel_id)
                .user_email(request.getUser_email())
                .user_name(request.getUser_name())
                .build();

        chatOperationUseCase.inviteMyFriend(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    //TODO(~10/27)
    @GetMapping("/{channel_id}/find")
    @ApiOperation(value = "채팅 내역 검색")
    public ResponseEntity<ApiResponseView<ChatRecordsView>> findContentInChannel(@PathVariable int channel_id,
                                                                                 @RequestParam(value = "content", required = false) String content) {

        logger.info("채팅 내역 검색");

        if (content == null || content.isEmpty()){
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        ChatReadUseCase.ContentFindQuery command = new ChatReadUseCase.ContentFindQuery(channel_id, content);
        var result =  chatReadUseCase.findContentInChannel(command);

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new ChatRecordsView(result)));
    }

    //-------------------------------------------------------------------------------------
    @DeleteMapping("/{channel_id}/{chat_id}")
    public void deleteList(@PathVariable int channel_id, @PathVariable int chat_id) {
        chatOperationUseCase.deleteOne(channel_id, chat_id);
    }
}