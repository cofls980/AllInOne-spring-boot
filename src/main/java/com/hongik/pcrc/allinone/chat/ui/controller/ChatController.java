package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.board.application.service.SearchEnum;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelEntity;
import com.hongik.pcrc.allinone.chat.application.service.ChatService;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChannelCreateRequest;
import com.hongik.pcrc.allinone.chat.ui.view.ChatRecordsView;
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

    private final ChatService chatService;
    private final ChatMapperRepository chatMapperRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatController(ChatService chatService, ChatMapperRepository chatMapperRepository) {
        this.chatService = chatService;
        this.chatMapperRepository = chatMapperRepository;
    }

    @GetMapping("")
    @ApiOperation(value = "채널 목록 (+검색)")
    public ResponseEntity<List<ChannelEntity>> searchChannel(@RequestParam(value = "title", required = false) String title) {

        SearchEnum searchEnum;

        if (title == null || title.isEmpty()){
            logger.info("전체 채널 목록");
            searchEnum = SearchEnum.NOTHING;
        } else {
            logger.info("채널 검색");
            searchEnum = SearchEnum.TITLE;
        }

        var result = chatService.searchChannel(searchEnum, title);
        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    @ApiOperation(value = "채널 생성")
    public ResponseEntity<ApiResponseView<SuccessView>> createChannel(@Valid @RequestBody ChannelCreateRequest request) {

        logger.info("채널 생성");

        chatService.createChannel(request.getCh_title());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping("/{channel_id}")
    @ApiOperation(value = "대화 목록")
    public ResponseEntity<ApiResponseView<ChatRecordsView>> enterChannel(@PathVariable int channel_id) {// 채팅방 입장

        logger.info("대화 목록");

        var result =  chatService.enterChannel(channel_id);

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        var users = chatMapperRepository.getUsersInChannel(channel_id);

        return ResponseEntity.ok(new ApiResponseView<>(new ChatRecordsView(users, result)));
    }

    @GetMapping("/my")
    @ApiOperation(value = "내가 등록한 채널 목록")
    public ResponseEntity<List<ChannelEntity>> getMyChannels() {

        logger.info("내가 등록한 채널 목록");

        var result = chatService.getMyChannels();
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

        chatService.leaveChannel(channel_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    //-------------------------------------------------------------------------------------
    /*@DeleteMapping("/{channel_id}")// 채팅방 삭제 - 대화 기록 삭제 및 채팅목록에서 삭제
    public void clearChannel(@PathVariable int channel_id) {
        chatService.clearChannel(channel_id);
    }
*/
    @DeleteMapping("/{channel_id}/{chat_id}")
    public void deleteList(@PathVariable int channel_id, @PathVariable int chat_id) {
        chatService.deleteOne(channel_id, chat_id);
    }
}
// 모든 create에서 스페이스만 있는 경우 처리