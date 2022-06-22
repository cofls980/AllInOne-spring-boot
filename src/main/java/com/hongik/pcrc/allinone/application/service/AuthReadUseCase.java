package com.hongik.pcrc.allinone.application.service;

import com.hongik.pcrc.allinone.application.domain.Auth;
import lombok.*;

public interface AuthReadUseCase {
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    class AuthFindQuery {
        private String authId;

        public AuthFindQuery(String authId) {
            this.authId = authId;
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
