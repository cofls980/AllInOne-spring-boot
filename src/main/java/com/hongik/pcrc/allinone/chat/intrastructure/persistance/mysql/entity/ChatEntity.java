package com.hongik.pcrc.allinone.chat.intrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.chat.application.domain.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "chat_test")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chat_id;
    @Column(nullable = false)
    private int channel_id;
    @Column(nullable = false, length = 320)
    private String user_email;
    @Column(nullable = false)
    private String user_name;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ChatEntity(Chat chat) {
        this.channel_id = chat.getChannel_id();
        this.user_email = chat.getUser_email();
        this.user_name = chat.getUser_name();
        this.content = chat.getContent();
        this.type = chat.getType();
        this.timestamp = chat.getTimestamp();
    }
}
