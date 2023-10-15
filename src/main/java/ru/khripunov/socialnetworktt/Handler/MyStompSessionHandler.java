package ru.khripunov.socialnetworktt.Handler;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.khripunov.socialnetworktt.configuration.WebSocketConfig;
import ru.khripunov.socialnetworktt.dto.SimpMessage;

import java.lang.reflect.Type;


public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private Logger logger = LogManager.getLogger(MyStompSessionHandler.class);

    private Long chatId;
    private String message;

    public MyStompSessionHandler(Long chatId, String message) {
        this.chatId=chatId;
        this.message=message;
    }


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
        session.subscribe(WebSocketConfig.SIMPLE_BROKER_DESTINATION + "/" + Long.toString(chatId), this);
        logger.info("Subscribed to "+ WebSocketConfig.SIMPLE_BROKER_DESTINATION + "/" + Long.toString(chatId));
        session.send(WebSocketConfig.APP_DESTINATION_PREFIX + "/"  + Long.toString(chatId) + "/send", new SimpMessage(message));
        logger.info("Message sent to websocket server:" + WebSocketConfig.APP_DESTINATION_PREFIX + "/"  + Long.toString(chatId) + "/send");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return SimpMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        SimpMessage msg = (SimpMessage) payload;
        logger.info("Received : " + msg.getMessage() + " headers : " + headers);
    }

}
