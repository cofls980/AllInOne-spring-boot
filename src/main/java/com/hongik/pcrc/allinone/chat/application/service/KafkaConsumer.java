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

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = KafkaConstants.GROUP_ID)
    public void consume(KafkaMessage message) {
        try {
            template.convertAndSend("/topic/kafka-chat", message);// convert the message to send that to WebSocket topic
        } catch (Exception e) {
            System.out.println("error: " + e);
        }

        System.out.println("Listen: " + message);
    }
}

/*
    //SimpMessageSendingOperations template;
* logger.info("Consumed Message : " + message);
        HashMap<String, String> msg = new HashMap<>();
        msg.put("channel_id", String.valueOf(message.getChannel_id()));
        msg.put("user_email", message.getUser_email());
        msg.put("user_name", message.getUser_name());
        msg.put("content", message.getContent());
        msg.put("type", message.getType());
        msg.put("timestamp", String.valueOf(message.getTimestamp()));

        ObjectMapper mapper = new ObjectMapper();
        template.convertAndSend("/topic/kafka-chat", mapper.writeValueAsString(msg));
* */
