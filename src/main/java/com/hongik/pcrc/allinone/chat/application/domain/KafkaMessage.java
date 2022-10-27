package com.hongik.pcrc.allinone.chat.application.domain;

import com.hongik.pcrc.allinone.chat.application.service.KafkaOperationUseCase;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class KafkaMessage implements Serializable { // 이동되는 데이터 형식
    private int channel_id;
    private String user_email;
    private String user_name;
    private String fileName;
    private String type;
    private String content;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "{" + "\"channel_id\":\"" + channel_id + "\"" +
                ",\"user_email\":\"" + user_email + "\"" +
                ",\"user_name\":\"" + user_name + "\"" +
                ",\"fileName\":\"" + fileName + "\"" +
                ",\"type\":\"" + type + "\"" +
                ",\"content\":\"" + content + "\"" +
                ",\"timestamp\":\"" + timestamp + "\"}";
    }

    public KafkaMessage() {

    }

    public KafkaMessage(KafkaOperationUseCase.KafkaImageCommand chat) {
        this.channel_id = chat.getChannel_id();
        this.user_email = chat.getUser_email();
        this.user_name = chat.getUser_name();
        this.fileName = chat.getFileName();
        this.type = chat.getType();
        this.content = chat.getContent();
        this.timestamp = chat.getTimestamp();
    }

    public KafkaMessage(Chat chat) {
        this.channel_id = chat.getChannel_id();
        this.user_email = chat.getUser_email();
        this.user_name = chat.getUser_name();
        this.type = chat.getType();
        this.content = chat.getContent();
        this.timestamp = chat.getTimestamp();
    }
}
