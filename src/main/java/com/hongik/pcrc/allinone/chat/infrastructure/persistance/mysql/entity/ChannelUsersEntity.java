package com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.chat.application.domain.Channel;
import com.hongik.pcrc.allinone.chat.application.domain.ChannelUsers;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "channel_users")
public class ChannelUsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int channel_users_id;
    @Column(nullable = false)
    private int channel_id;
    @Column(nullable = false, length = 320)
    private String user_email;
    @Column(nullable = false)
    private String user_name;

    public ChannelUsersEntity(ChannelUsers channelUsers) {
        this.channel_id = channelUsers.getChannel_id();
        this.user_email = channelUsers.getUser_email();
        this.user_name = channelUsers.getUser_name();
    }
}
