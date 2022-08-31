package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthEntityRepository extends CrudRepository<AuthEntity, String> {

    Auth findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("update users_test u set u.password = :pwd where u.id = :id")
    void updatePwd(UUID id, String pwd);

    void deleteByEmail(String email);

    @Query("update users_test u set u.refresh_token = :refreshToken where u.id = :id")
    void updateRefreshToken(UUID id, String refreshToken);

}
