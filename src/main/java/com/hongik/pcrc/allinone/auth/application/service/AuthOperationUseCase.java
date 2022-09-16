package com.hongik.pcrc.allinone.auth.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

public interface AuthOperationUseCase {

    void createAuth(AuthCreatedCommand command);
    void updateAuth(AuthUpdateCommand command);
    void deleteAuth();
    void updateRefreshToken(UUID id, String refreshToken);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class AuthCreatedCommand{
        //Auth Info
        private final String email;
        private final String password;
        private final String name;
        private final String birth;
        private final String gender;
        private final String phone_number;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class AuthUpdateCommand{ //pwd update
        //Auth Info
        private final String email;
        private final String password;
    }
}
