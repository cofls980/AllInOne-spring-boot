package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.chat.intrastructure.persistance.mysql.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.application.service.ChatService;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/chat")
@Api(tags = {"Chat API"})
public class ChatController {

    private final ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("")
    @ApiOperation(value = "대화 목록")
    public ResponseEntity<List<ChatEntity>> getChatList() {

        logger.info("대화 목록");

        var result =  chatService.getChatList();

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("")
    public void clearChannel() {
        chatService.clearChannel();
    }

    @DeleteMapping("/{chat_id}")
    public void deleteList(@PathVariable int chat_id) {
        chatService.deleteOne(chat_id);
    }
}
