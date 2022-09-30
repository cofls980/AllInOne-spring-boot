package com.hongik.pcrc.allinone.chat.application.service;

import com.hongik.pcrc.allinone.chat.intrastructure.persistance.mysql.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.intrastructure.persistance.mysql.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatEntity> getChatList() {//pathVariable

        int ch = 0;

        return chatRepository.findByChannel_id(ch);
    }

    public void clearChannel() {
        chatRepository.deleteAll();
    }

    public void deleteOne(int chat_id) {
        chatRepository.deleteById(chat_id);
    }
}
