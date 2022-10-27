package com.hongik.pcrc.allinone.chat.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public interface KafkaOperationUseCase {
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class KafkaImageCommand {
        private final int channel_id;
        private final String user_email;
        private final String user_name;
        private final String content;
        private final String fileName;
        private final String type;
        private final LocalDateTime timestamp;
    }
}
