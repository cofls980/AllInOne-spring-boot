package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.chat.application.service.KafkaProducer;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChatSendRequest;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v2/chat")
public class KafkaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> sendMessage(@Valid @RequestBody ChatSendRequest request) {//@PathVariable int channel_id,
        // later resolve channel_id
        logger.info("메시지 전송");

        producer.sendMessage(request.getContent());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    /*@MessageMapping("sendMessage") // config의 prefix, endpoint 설정을 포함한 입력한 url로 발행된 메시지 구독
    @SendTo("/topic/kafka-chat") // 해당 메서드의 return 값을 url을 구독하는 클라이언트에게 메시지 발행
    public KafkaMessage listenChat(@Payload KafkaMessage message) {
        System.out.println("Received: " + message);
        return message;
    }*/
}
