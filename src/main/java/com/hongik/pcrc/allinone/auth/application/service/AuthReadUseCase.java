package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import lombok.*;

import javax.naming.AuthenticationException;
import java.util.UUID;

public interface AuthReadUseCase {

    FindAuthResult getAuth(AuthFindQuery query) throws AuthenticationException;

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class AuthFindQuery {
        private String email;
        private String password;

        public AuthFindQuery(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    @ToString
    @Builder
    class FindAuthResult {
        // Auth Info
        private final UUID id;
        private final String email;
        private final String name;
        private final String birth;
        private final String gender;
        private final String phone_number;

        public static FindAuthResult findByAuth(Auth auth) {
            return FindAuthResult.builder()
                    .id(auth.getId())
                    .email(auth.getEmail())
                    .name(auth.getName())
                    .birth(auth.getBirth())
                    .gender(auth.getGender())
                    .phone_number(auth.getPhone_number())
                    .build();
        }
    }


}
