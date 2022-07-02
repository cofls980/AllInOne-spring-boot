package com.hongik.pcrc.allinone.ui.requestBody;

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
    private String id;
    private String password;
}
