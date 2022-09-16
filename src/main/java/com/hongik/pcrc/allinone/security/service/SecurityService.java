package com.hongik.pcrc.allinone.security.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final JwtProvider jwtProvider;

    private final AuthEntityRepository authEntityRepository;
    private final AuthMapperRepository authMapperRepository;

    public SecurityService(JwtProvider jwtProvider, AuthEntityRepository authEntityRepository, AuthMapperRepository authMapperRepository) {
        this.jwtProvider = jwtProvider;
        this.authEntityRepository = authEntityRepository;
        this.authMapperRepository = authMapperRepository;
    }

    public boolean validateRefreshToken(String requestToken) {

        String authId = jwtProvider.getUserInfo(requestToken);

        var auth = authEntityRepository.findByEmail(authId);

        if (auth.isEmpty()) {
            throw new AllInOneException(MessageType.ReLogin);
        }

        return auth.get().getRefresh_token().equals(requestToken);
    }

    public String createNewAccessToken(String refreshToken) {

        String authId = jwtProvider.getUserInfo(refreshToken);

        return jwtProvider.createAccessToken(authId);
    }

}
