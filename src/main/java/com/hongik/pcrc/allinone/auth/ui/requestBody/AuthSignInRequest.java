package com.hongik.pcrc.allinone.auth.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthSignInRequest {

    //Auth Sign In Info
    private String user_id;
    private String password;
}
