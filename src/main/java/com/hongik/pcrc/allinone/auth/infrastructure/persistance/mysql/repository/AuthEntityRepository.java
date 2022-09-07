package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthEntityRepository extends CrudRepository<AuthEntity, UUID> {


    @Query(value = "select * from users u where u.email = ?1", nativeQuery = true)
    Optional<AuthEntity> findByEmail(String email);
}
