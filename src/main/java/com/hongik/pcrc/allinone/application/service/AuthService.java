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

    @Override
    public String updateAuth(AuthUpdateCommand command) {
        var authEntity = authRepository.findById(command.getId());

        if (authEntity.isPresent()) {
            boolean isSame = passwordEncoder.matches(command.getPassword(), authEntity.get().getPassword());
            if (isSame)
                return "conflict";
            String encodePassword = passwordEncoder.encode(command.getPassword());
            var auth = Auth.builder()
                    .id(command.getId())
                    .password(encodePassword)
                    .name(authEntity.get().getName())
                    .birth(authEntity.get().getBirth())
                    .gender(authEntity.get().getGender())
                    .phoneNumber(authEntity.get().getPhoneNumber())
                    .build();
            var entity = new AuthEntity(auth);
            var result = authRepository.save(entity);
            return "true";
        }
        return "not_found";
    }

    @Override
    public FindAuthResult getAuth(AuthFindQuery query) {

        var authEntity = authRepository.findById(query.getAuthId());

        if (authEntity.isPresent()) {
            boolean result = passwordEncoder.matches(query.getPassword(), authEntity.get().getPassword());
            if (result)
                return FindAuthResult.findByAuth(authEntity.get().toAuth());
        }
        return null;
    }
}
