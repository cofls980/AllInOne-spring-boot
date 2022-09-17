package com.hongik.pcrc.allinone.security.service;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import com.hongik.pcrc.allinone.security.jwt.TokenInfo;
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

        String email = (String) jwtProvider.parseClaims(requestToken).get("email");

        var auth = authEntityRepository.findByEmail(email);

        if (auth.isEmpty()) {
            throw new AllInOneException(MessageType.ReLogin);
        }

        return auth.get().getRefresh_token().equals(requestToken);
    }

    public TokenInfo createNewAccessToken(String refreshToken) {

        String email = (String) jwtProvider.parseClaims(refreshToken).get("email");

        return jwtProvider.generateToken(email);
    }

}
