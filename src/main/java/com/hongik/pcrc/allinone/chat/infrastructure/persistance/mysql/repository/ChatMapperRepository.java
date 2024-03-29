package com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChannelUsersEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface ChatMapperRepository {

    // channel table
    boolean isExistedTitle(@Param("ch_title") String ch_title);
    boolean notExistedChannel(@Param("channel_id") int channel_id);
    List<HashMap<String, Object>> getChannelList(@Param("list") ArrayList<String> list);
    ChannelEntity findChannelInfo(@Param("channel_id") int channel_id);
    int getChannelIdByTitle(@Param("ch_title") String ch_title);

    void createChannel(ChannelEntity channelEntity);
    void increaseChannelNumberOfUsers(@Param("channel_id") int channel_id);
    void decreaseChannelNumberOfUsers(@Param("channel_id") int channel_id);
    void deleteChannel(@Param("channel_id") int channel_id);

    // chat table
    List<HashMap<String, Object>> getRecordsInChannel(@Param("channel_id") int channel_id);
    List<HashMap<String, Object>> findContentInChannel(@Param("channel_id") int channel_id, @Param("content") String content);

    void createRecord(ChatEntity chatEntity);
    void deleteAllRecordsInChannel(int channel_id);
    void changeUserRecordsNon(@Param("user_email") String user_email);

    // channel_users table
    boolean isExistedUser(@Param("channel_id") int channel_id, @Param("user_email") String user_email);
    List<HashMap<String, Object>> getChannelsOfUser(@Param("user_email") String user_email);
    List<ChannelUsersEntity> getUsersInChannel(@Param("channel_id") int channel_id);

    void addUserAboutChannel(ChannelUsersEntity channelUsersEntity);
    void leaveTheChannel(@Param("channel_id") int channel_id, @Param("user_email") String user_email);

    // mix
    List<HashMap<String, Object>> getMyChannelList(@Param("user_email") String user_email);

    // clear
    void deleteAllInChannel(@Param("channel_id") int channel_id);
    void deleteAllInChannelNum(@Param("channel_id") int channel_id, @Param("chat_id") int chat_id);
}
