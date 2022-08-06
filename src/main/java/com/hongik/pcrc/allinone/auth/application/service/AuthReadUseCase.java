package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import lombok.*;

import javax.naming.AuthenticationException;

public interface AuthReadUseCase {

    FindAuthResult getAuth(AuthFindQuery query) throws AuthenticationException;

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class AuthFindQuery {
        private String authId;
        private String password;

        public AuthFindQuery(String authId, String password) {
            this.authId = authId;
            this.password = password;
        }
    }

    @Getter
    @ToString
    @Builder
    class FindAuthResult {
        // Auth Info
        private final String id;
        private final String name;
        private final String birth;
        private final String gender;
        private final String phoneNumber;

        public static FindAuthResult findByAuth(Auth auth) {
            return FindAuthResult.builder()
                    .id(auth.getId())
                    .name(auth.getName())
                    .birth(auth.getBirth())
                    .gender(auth.getGender())
                    .phoneNumber(auth.getPhoneNumber())
                    .build();
        }
    }
}
