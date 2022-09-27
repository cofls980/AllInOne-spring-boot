package com.hongik.pcrc.allinone.chat.application.domain;

import org.springframework.beans.factory.annotation.Value;

public class KafkaConstants {
    public static final String KAFKA_TOPIC = "kafka-chat";
    public static final String GROUP_ID = "foo";
    //@Value("${}")
    public static final String KAFKA_BROKER = "";
}
