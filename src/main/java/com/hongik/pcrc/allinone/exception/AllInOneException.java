package com.hongik.pcrc.allinone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AllInOneException extends RuntimeException {

    private final HttpStatus status;
    private final String type;

    public AllInOneException(MessageType messageType) {
        super(messageType.getMessage());
        this.status = messageType.getStatus();
        this.type = messageType.name();
    }
}
