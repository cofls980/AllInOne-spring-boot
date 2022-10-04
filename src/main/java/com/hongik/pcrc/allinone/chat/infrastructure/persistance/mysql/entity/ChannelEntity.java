package com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.chat.application.domain.Channel;
import com.hongik.pcrc.allinone.chat.application.domain.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "channel")
public class ChannelEntity { // 채팅방을 등록한 사용자 기록을 가지고 있어야함.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int channel_id;
    @Column(nullable = false)
    private String ch_title; // 길이 설정해야함
    @Column(nullable = false)
    private int number_of_users;
    @Column(nullable = false)
    private LocalDateTime created_date;

    public ChannelEntity(Channel channel) {
        this.ch_title = channel.getCh_title();
        this.number_of_users = channel.getChannel_id();
        this.created_date = channel.getCreated_date();
    }
}
