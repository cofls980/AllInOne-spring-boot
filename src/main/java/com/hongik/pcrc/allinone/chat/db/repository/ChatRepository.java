package com.hongik.pcrc.allinone.chat.db.repository;

import com.hongik.pcrc.allinone.chat.db.entity.ChatEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Integer> {

    @Query(value = "select * from chat_test where channel_id = ?1", nativeQuery = true)
    List<ChatEntity> findByChannel_id(int channel_id);

}
