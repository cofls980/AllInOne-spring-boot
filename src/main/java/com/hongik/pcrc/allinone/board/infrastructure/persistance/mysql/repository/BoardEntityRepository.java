package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//@Mapper
@Repository
public interface BoardEntityRepository extends CrudRepository<BoardEntity, Integer> {
     //List<Map<String, Object>> getBoards();
}