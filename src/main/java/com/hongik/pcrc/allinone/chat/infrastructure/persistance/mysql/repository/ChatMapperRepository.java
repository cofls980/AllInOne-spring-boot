package com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapperRepository {

    // channel table
    boolean isExistedTitle(@Param("ch_title") String ch_title);
    boolean isExistedChannel(@Param("channel_id") int channel_id);
    List<ChannelEntity> getChannelList();
    List<ChannelEntity> searchChannelListWithTitle(@Param("ch_title") String ch_title);
    ChannelEntity findChannelInfo(@Param("channel_id") int channel_id);
    int getChannelIdByTitle(@Param("ch_title") String ch_title);

    void createChannel(ChannelEntity channelEntity);
    void increaseChannelNumberOfUsers(@Param("channel_id") int channel_id);

    // chat table
    List<ChatEntity> getRecordsInChannel(@Param("channel_id") int channel_id);

    void createRecord(ChatEntity chatEntity);

    // channel_users table
    boolean isExistedUser(@Param("channel_id") int channel_id, @Param("user_email") String user_email);
    List<ChannelUsersEntity> getUsersInChannel(@Param("channel_id") int channel_id);

    void addUserAboutChannel(ChannelUsersEntity channelUsersEntity);

    // mix
    void leaveTheChannelLast(int channel_id); // delete chat and channel table
    void leaveTheChannel(int channel_id);

    // clear
    void deleteAllInChannel(@Param("channel_id") int channel_id);
    void deleteAllInChannelNum(@Param("channel_id") int channel_id, @Param("chat_id") int chat_id);
}
