package com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.entity.EmailEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailEntityRepository  extends CrudRepository<EmailEntity, String> {
}
