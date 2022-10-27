package com.hongik.pcrc.allinone.chat.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaConstants;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @Autowired
    SimpMessagingTemplate template;

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = "dooli")
    public void consume(KafkaMessage message) {
        try {
            // convert the message to send that to WebSocket topic
            // message type is string
            template.convertAndSend("/topic/kafka-chat/" + message.getChannel_id(), mapper.writeValueAsString(message));
            System.out.println("[" + message.getChannel_id() + "]Consume: " + mapper.writeValueAsString(message));
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }
}
// 파티션과 연결된 컨슈머 그룹이 메시지를 받는다
// 이 때 다른 서버 2개가 같은 컨슈머 그룹으로 consume 하려하면 서로 컨슈머 그룹을 차지하려한다.
// 따라서 서버 2개가 같은 파티션을 통해 consume 하려면 서로 다른 컨슈머 그룹을 차지해야한다.