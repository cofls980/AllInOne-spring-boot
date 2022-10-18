package com.hongik.pcrc.allinone.chat.ui.requestBody;

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
public class InviteFriendRequest {

    @NotNull
    @NotBlank
    @Email
    private String user_email;
    @NotNull
    @NotBlank
    private String user_name;
}
