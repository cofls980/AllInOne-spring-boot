package com.hongik.pcrc.allinone.security.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final JwtProvider jwtProvider;

    private final AuthEntityRepository authEntityRepository;

    public SecurityService(JwtProvider jwtProvider, AuthEntityRepository authEntityRepository) {
        this.jwtProvider = jwtProvider;
        this.authEntityRepository = authEntityRepository;
    }

    public boolean validateRefreshToken(String requestToken) {

        String authId = jwtProvider.getUserInfo(requestToken);

        var auth = authEntityRepository.findById(authId);

        if (auth.isEmpty()) {
            throw new AllInOneException(MessageType.ReLogin);
        }

        if (!auth.get().getRefresh_token().equals(requestToken))
            return false;
        return true;
    }

    public String createNewAccessToken(String refreshToken) {

        String authId = jwtProvider.getUserInfo(refreshToken);

        String accessToken = jwtProvider.createAccessToken(authId);

        return accessToken;
    }

}
