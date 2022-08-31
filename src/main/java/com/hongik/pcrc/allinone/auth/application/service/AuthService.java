package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public void createAuth(AuthCreatedCommand command) {

        var query = authRepository.existsByEmail(command.getEmail());
        if (query) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        var auth = Auth.builder()
                .id(UUID.randomUUID())
                .email(command.getEmail())
                .password(encodePassword)
                .name(command.getName())
                .birth(command.getBirth())
                .gender(command.getGender())
                .phoneNumber(command.getPhoneNumber())
                .build();

        var authEntity = new AuthEntity(auth);

        authRepository.save(authEntity);
    }

    @Override
    public void updateAuth(AuthUpdateCommand command) {
        /*var authEntity = authRepository.findById(command.getId());

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
                    .phoneNumber(authEntity.get().getPhone_number())
                    .build();
            var entity = new AuthEntity(auth);
            authRepository.save(entity);
            return "true";
        }
        return "not_found";*/

        var auth = authRepository.findByEmail(command.getEmail());

        if (auth == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        if (passwordEncoder.matches(command.getPassword(), auth.getPassword())) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        authRepository.updatePwd(auth.getId(), encodePassword);
        /*var result = Auth.builder()
                .id(auth.getId())
                .email(auth.getEmail())
                .password(encodePassword)
                .name(auth.getName())
                .birth(auth.getBirth())
                .gender(auth.getGender())
                .phoneNumber(auth.getPhoneNumber())
                .build();

        AuthEntity authEntity = new AuthEntity(result);
        authRepository.save(authEntity);*/
        //authMapperRepository.updateUser(makeUser(command.getEmail(), encodePassword));
    }

    @Override
    public void deleteAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();

        if (!authRepository.existsByEmail(email)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        authRepository.deleteByEmail(email);

        /*var authEntity = authRepository.findById(id);

        if (authEntity.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        authRepository.deleteById(authEntity.get().getId());*/
    }

    @Override
    public FindAuthResult getAuth(AuthFindQuery query) {

        /*var authEntity = authRepository.findById(query.getEmail());

        if (authEntity.isEmpty() || passwordEncoder.matches(query.getPassword(), authEntity.get().getPassword())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return FindAuthResult.findByAuth(authEntity.get().toAuth());*/

        var auth = authRepository.findByEmail(query.getEmail());

        if (auth == null || passwordEncoder.matches(query.getPassword(), auth.getPassword())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        return FindAuthResult.findByAuth(auth);
    }

    @Override
    public void updateRefreshToken(String email, String refreshToken) {

        var auth = authRepository.findByEmail(email);

        authRepository.updateRefreshToken(auth.getId(), auth.getRefreshToken());

        /*var entity = authRepository.findById(id);

        var auth = Auth.builder()
                .id(id)
                .password(entity.get().getPassword())
                .name(entity.get().getName())
                .birth(entity.get().getBirth())
                .gender(entity.get().getGender())
                .phoneNumber(entity.get().getPhone_number())
                .refreshToken(refreshToken)
                .build();

        var authEntity = new AuthEntity(auth);

        authRepository.save(authEntity);*/
    }
}
