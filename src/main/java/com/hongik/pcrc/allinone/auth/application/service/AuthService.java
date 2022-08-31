package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity.AuthEntity;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
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
    private final AuthMapperRepository authMapperRepository;

    @Autowired
    public AuthService(AuthEntityRepository authRepository, PasswordEncoder passwordEncoder, AuthMapperRepository authMapperRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.authMapperRepository = authMapperRepository;
    }

    @Override
    public void createAuth(AuthCreatedCommand command) {

        var query = authMapperRepository.existsByEmail(command.getEmail());
        if (query) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        var auth = Auth.builder()
                .email(command.getEmail())
                .password(encodePassword)
                .name(command.getName())
                .birth(command.getBirth())
                .gender(command.getGender())
                .phone_number(command.getPhone_number())
                .build();

        var authEntity = new AuthEntity(auth);

        authRepository.save(authEntity);
    }

    @Override
    public void updateAuth(AuthUpdateCommand command) {

        var auth = authRepository.findByEmail(command.getEmail());

        if (auth.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        if (passwordEncoder.matches(command.getPassword(), auth.get().getPassword())) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        authMapperRepository.updatePwd(makeMap(auth.get().getId(), "password", encodePassword));
    }

    @Override
    public void deleteAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();

        if (!authMapperRepository.existsByEmail(email)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        authMapperRepository.deleteByEmail(email);
    }

    @Override
    public FindAuthResult getAuth(AuthFindQuery query) {

        var auth = authRepository.findByEmail(query.getEmail());

        if (auth.isEmpty() || !passwordEncoder.matches(query.getPassword(), auth.get().getPassword())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        return FindAuthResult.findByAuth(auth.get().toAuth());
    }

    @Override
    public void updateRefreshToken(UUID id, String refresh_token) {

        authMapperRepository.updateRefreshToken(makeMap(id, "refresh_token", refresh_token));
    }

    public Map<String, Object> makeMap(UUID id, String key, String value) {

        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put(key, value);

        return map;
    }
}
