package com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsEntityRepository extends CrudRepository<CommentsEntity, Integer> {

    @Query(value = "select * from comments where board_id = ?1", nativeQuery = true)
    List<CommentsEntity> findByBoard_id(int board_id);
}
