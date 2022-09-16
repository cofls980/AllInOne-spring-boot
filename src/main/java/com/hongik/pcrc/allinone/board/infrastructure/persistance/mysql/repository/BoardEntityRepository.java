package com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardEntityRepository extends CrudRepository<BoardEntity, Integer> {

    @Query(value = "select * from boards", nativeQuery = true)
    List<BoardEntity> findAll();

    @Query(value = "select * from boards where boards.title like CONCAT('%',?1,'%')", nativeQuery = true)
    List<BoardEntity> findWithTitle(String title);

    @Query(value = "select boards.board_id, boards.b_date, boards.content, boards.title, boards.user_id " +
            "from boards, users where users.name = ?1 and users.id = boards.user_id", nativeQuery = true)
    List<BoardEntity> findWithWriter(String b_writer);

    @Query(value = "select distinct boards.board_id, boards.b_date, boards.content, boards.title, boards.user_id " +
            "from boards, users where boards.title like CONCAT('%',?1,'%') or (users.name = ?2 and users.id = boards.user_id)", nativeQuery = true)
    List<BoardEntity> findWithTitleAndWriter(String title, String b_writer);
}
