package com.hongik.pcrc.allinone.auth.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface AuthOperationUseCase {

    AuthReadUseCase.FindAuthResult createAuth(AuthCreatedCommand command);
    String updateAuth(AuthUpdateCommand command);
    void deleteAuth();
    void updateRefreshToken(String id, String refreshToken);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class AuthCreatedCommand{
        //Auth Info
        private final String id;
        private final String password;
        private final String name;
        private final String birth;
        private final String gender;
        private final String phoneNumber;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class AuthUpdateCommand{
        //Auth Info
        private final String id;
        private final String password;
        private final String name;
        private final String birth;
        private final String gender;
        private final String phoneNumber;
    }
}
