package com.hongik.pcrc.allinone.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthCreateRequest {

    //Auth Info
    private String id;
    private String password;
    private String name;
    private String birth;
    private String gender;
    private String phoneNumber;
}
