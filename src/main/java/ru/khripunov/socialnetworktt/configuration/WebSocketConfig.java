package ru.khripunov.socialnetworktt.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String SIMPLE_BROKER_DESTINATION = "/topic";
    public static final String APP_DESTINATION_PREFIX = "/app";
    public static final String END_POINT = "/ws";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker( SIMPLE_BROKER_DESTINATION);
        config.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);


    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(END_POINT);
    }


}

