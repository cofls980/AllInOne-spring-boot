package com.hongik.pcrc.allinone.chat.application.service;

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

    /*@Autowired
    private CustomWebSocketHandler customWebSocketHandler;*/

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = "foo")
    public void listen(KafkaMessage message) {
        try {
            template.convertAndSend("/topic/kafka-chat", message);// convert the message to send that to WebSocket topic
        } catch (Exception e) {
            System.out.println("error: " + e);
        }

        System.out.println("Listen: " + message);
    }
}
