package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import com.hongik.pcrc.allinone.chat.db.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.application.service.ChatService;
import com.hongik.pcrc.allinone.chat.application.service.KafkaProducer;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChatSendRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/chat")
@Api(tags = {"Chat API"})
public class ChatController {

    private final KafkaProducer producer;
    private final ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatController(KafkaProducer producer, ChatService chatService) {
        this.producer = producer;
        this.chatService = chatService;
    }

    @PostMapping("/publish")
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> sendMessage(@RequestBody ChatSendRequest request) {//@PathVariable int channel_id,
        // later resolve channel_id and user
        logger.info("메시지 전송");

        producer.sendMessage(request.getContent());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
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

    @MessageMapping("sendMessage") // config의 prefix, endpoint 설정을 포함한 입력한 url로 발행된 메시지 구독
    @SendTo("/topic/kafka-chat") // 해당 메서드의 return 값을 url을 구독하는 클라이언트에게 메시지 발행
    public KafkaMessage listenChat(@Payload KafkaMessage message) {
        System.out.println("Received: " + message);
        return message;
    }

    /* <고려해야할 경우>
    * - 사용자가 브라우저를 끄거나 다른 경로로 이동할 경우
    * -
    * */
}
