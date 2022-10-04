package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.chat.application.service.KafkaProducer;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChatSendRequest;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v2/chat")
public class KafkaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/{channel_id}")
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> sendMessage(@PathVariable int channel_id, @Valid @RequestBody ChatSendRequest request) {

        logger.info("메시지 전송");

        producer.sendMessage(channel_id, request.getContent());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
