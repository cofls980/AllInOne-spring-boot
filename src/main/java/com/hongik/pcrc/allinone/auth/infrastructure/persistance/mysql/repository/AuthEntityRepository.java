package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthEntityRepository extends CrudRepository<AuthEntity, String> {

}
