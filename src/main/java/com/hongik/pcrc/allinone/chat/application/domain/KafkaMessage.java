package com.hongik.pcrc.allinone.chat.application.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class KafkaMessage implements Serializable {
    private int channel_id;
    private String user_email;
    private String user_name;
    private String content;
    private String type;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Message { " + "channel_id='" + channel_id + "'" +
                ", user_email='" + user_email + "'" +
                ", user_name='" + user_name + "'" +
                ", content='" + content + "'" +
                ", type='" + type + "'" +
                ", timestamp='" + timestamp + "' }";
    }

    public KafkaMessage() {

    }

    public KafkaMessage(Chat chat) {
        this.channel_id = chat.getChannel_id();
        this.user_email = chat.getUser_email();
        this.user_name = chat.getUser_name();
        this.content = chat.getContent();
        this.type = chat.getType();
        this.timestamp = chat.getTimestamp();
    }
}
