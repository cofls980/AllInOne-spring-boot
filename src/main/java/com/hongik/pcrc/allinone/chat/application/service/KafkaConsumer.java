package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.chat.application.domain.KafkaConstants;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @Autowired
    SimpMessagingTemplate template;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = KafkaConstants.GROUP_ID)
    public void consume(@Payload KafkaMessage message) {
        try {
            // convert the message to send that to WebSocket topic
            // message type is string
            // later add channel_id
            template.convertAndSend("/topic/kafka-chat", message.toString());
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        System.out.println("Listen: " + message);
    }
}
// 파티션을 가진 서버가 메시지를 받는다
// 파티션은 서버 당 하나로 생각 중