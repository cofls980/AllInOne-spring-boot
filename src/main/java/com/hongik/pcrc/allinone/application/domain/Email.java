package com.hongik.pcrc.allinone.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@Builder
public class Email {

    private final String id;
    private final String code;

    private final LocalDateTime createdDate;

}
