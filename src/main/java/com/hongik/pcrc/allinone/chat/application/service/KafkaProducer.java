package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaConstants;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import com.hongik.pcrc.allinone.chat.db.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.db.repository.ChatRepository;
import com.hongik.pcrc.allinone.chat.application.domain.Chat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final ChatRepository chatRepository;
    private final AuthEntityRepository authEntityRepository;

    public KafkaProducer(KafkaTemplate<String, KafkaMessage> kafkaTemplate, ChatRepository chatRepository, AuthEntityRepository authEntityRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatRepository = chatRepository;
        this.authEntityRepository = authEntityRepository;
    }

    public void sendMessage(String content) {

        int ch = 0;//PathVariable
        String email = getUserEmail();
        String name = authEntityRepository.findByEmailResultName(email);
        String type = "TEXT";//RequestBody

        var chat = Chat.builder()
                .channel_id(ch)
                .user_email(email)
                .user_name(name)
                .content(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();
        chatRepository.save(new ChatEntity(chat));
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, new KafkaMessage(chat));
        System.out.println("Produce message: " +  content);
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
