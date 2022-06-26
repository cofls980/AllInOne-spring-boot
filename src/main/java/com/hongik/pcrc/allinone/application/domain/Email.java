package com.hongik.pcrc.allinone.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Email {

    private final String id;
    private final String code;

}
