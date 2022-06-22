package com.hongik.pcrc.allinone.ui.view.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.pcrc.allinone.application.service.AuthReadUseCase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthView {

    private final String id;
    private final String name;
    private final String birth;
    private final String gender;
    private final String phoneNumber;

    public AuthView(AuthReadUseCase.FindAuthResult result) {
        this.id = result.getId();
        this.name = result.getName();
        this.birth = result.getBirth();
        this.gender = result.getGender();
        this.phoneNumber = result.getPhoneNumber();
    }
}
