package com.hongik.pcrc.allinone.application.service;

import com.hongik.pcrc.allinone.application.domain.Auth;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthOperationUseCase, AuthReadUseCase {

    private final AuthEntityRepository authRepository;

    @Autowired
    public AuthService(AuthEntityRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public FindAuthResult createAuth(AuthCreatedCommand command) {

        var auth = Auth.builder()
                .id(command.getId())
                .password(command.getPassword())
                .name(command.getName())
                .birth(command.getBirth())
                .gender(command.getGender())
                .phoneNumber(command.getPhoneNumber())
                .build();

        var authEntity = new AuthEntity(auth);
        var result = authRepository.save(authEntity);

        return FindAuthResult.findByAuth(result.toAuth());
    }

}
