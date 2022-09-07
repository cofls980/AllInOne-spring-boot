package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardEntityRepository extends CrudRepository<BoardEntity, Integer> {

    @Query(value = "select * from boards_test", nativeQuery = true)
    List<BoardEntity> findAll();
}
