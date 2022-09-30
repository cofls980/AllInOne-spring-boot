package com.hongik.pcrc.allinone.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").addInterceptors(new HttpHandShakeInterceptor()).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 메시지 브로커 구성
        // "/topic" 접두사가 붙은 url을 구독하는 대상들에 한하여 브로커가 메세지를 전달한다.
        // sub
        registry.enableSimpleBroker("/topic");
        // "/chat" 접두사가 붙은 url로 발행한 메세지만 핸들러로 라우팅
        // pub
        registry.setApplicationDestinationPrefixes("/app"); // 메시지를 전송할 때 사용하는 url
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(20 * 10000);
        registration.setSendBufferSizeLimit(10 * 52 * 1024);
    }
}
