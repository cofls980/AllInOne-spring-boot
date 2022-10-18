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
public class FriendCreateRequest {

    @Email
    @NotNull
    @NotBlank
    private String user_email;
    @NotNull @NotBlank
    private String user_name;
}
