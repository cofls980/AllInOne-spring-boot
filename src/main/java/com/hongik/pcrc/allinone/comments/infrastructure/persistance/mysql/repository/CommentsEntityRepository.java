package com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity.CommentsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsEntityRepository extends CrudRepository<CommentsEntity, Integer> {
}
