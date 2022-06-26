package com.hongik.pcrc.allinone.application.service;

import com.hongik.pcrc.allinone.application.domain.Auth;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthOperationUseCase, AuthReadUseCase {

    private final AuthEntityRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthEntityRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public FindAuthResult createAuth(AuthCreatedCommand command) {

        var query = authRepository.existsById(command.getId());
        if (query) {
            return FindAuthResult.findByAuth(Auth.builder()
                    .id("conflict")
                    .build());
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        var auth = Auth.builder()
                .id(command.getId())
                .password(encodePassword)
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
