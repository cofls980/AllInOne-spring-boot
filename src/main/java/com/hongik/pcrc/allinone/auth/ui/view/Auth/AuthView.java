package com.hongik.pcrc.allinone.auth.ui.view.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.auth.application.service.AuthReadUseCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthView {

    private final String user_id;
    private final String name;
    private final String birth;
    private final String gender;
    private final String phoneNumber;
    private final String accessToken;
    private final String refreshToken;

    public AuthView(AuthReadUseCase.FindAuthResult result, String accessToken, String refreshToken) {
        this.user_id = result.getId();
        this.name = result.getName();
        this.birth = result.getBirth();
        this.gender = result.getGender();
        this.phoneNumber = result.getPhoneNumber();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
