package com.hongik.pcrc.allinone.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Auth {

    private final String id;
    private final String password;
    private final String name;
    private final String birth;
    private final String gender;
    private final String phoneNumber;

}
