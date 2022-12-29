package com.hongik.pcrc.allinone.auth.application.service;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import lombok.*;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AuthReadUseCase {

    FindAuthResult getAuth(AuthFindQuery query) throws AuthenticationException, IOException;
    List<FindMyFriendResult> getMyFriendList();

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
        private final String profile;

        public static FindAuthResult findByAuth(Auth auth, String bytes) {
            return FindAuthResult.builder()
                    .id(auth.getId())
                    .email(auth.getEmail())
                    .name(auth.getName())
                    .birth(auth.getBirth())
                    .gender(auth.getGender())
                    .phone_number(auth.getPhone_number())
                    .profile(bytes)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindMyFriendResult {
        private final int friend_id;
        private final String user_email;
        private final String user_name;

        public static FindMyFriendResult findByAuth(int friend_id, Auth auth) {
            return FindMyFriendResult.builder()
                    .friend_id(friend_id)
                    .user_email(auth.getEmail())
                    .user_name(auth.getName())
                    .build();
        }
    }
}
