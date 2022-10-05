package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaConstants;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.chat.application.domain.Chat;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final AuthEntityRepository authEntityRepository;
    private final ChatMapperRepository chatMapperRepository;

    public KafkaProducer(KafkaTemplate<String, KafkaMessage> kafkaTemplate,
                         AuthEntityRepository authEntityRepository, ChatMapperRepository chatMapperRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.authEntityRepository = authEntityRepository;
        this.chatMapperRepository = chatMapperRepository;
    }

    public void sendMessage(int channel_id, String content) {

        if (!chatMapperRepository.isExistedChannel(channel_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String name = authEntityRepository.findByEmailResultName(email);
        String type = "TEXT";//RequestBody

        var chat = Chat.builder()
                .channel_id(channel_id)
                .user_email(email)
                .user_name(name)
                .content(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, new KafkaMessage(chat));
        chatMapperRepository.createRecord(new ChatEntity(chat));
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
