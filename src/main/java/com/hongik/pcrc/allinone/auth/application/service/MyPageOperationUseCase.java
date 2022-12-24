package com.hongik.pcrc.allinone.auth.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MyPageOperationUseCase {

    void UpdateProfile(ProfileUpdateCommand command) throws IOException;

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class ProfileUpdateCommand{
        private final MultipartFile profile;
    }
}
