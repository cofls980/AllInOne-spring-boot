package com.hongik.pcrc.allinone.auth.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthCreateRequest {

    //Auth Info
    @Email @NotNull @NotBlank
    private String email;
    @NotNull @NotBlank
    private String password;
    @NotNull @NotBlank
    private String name;
    private String birth;
    private String gender;
    private String phoneNumber;
}
