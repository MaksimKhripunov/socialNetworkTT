package ru.khripunov.socialnetworktt.controller;

import lombok.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import ru.khripunov.socialnetworktt.configuration.WebSocketConfig;
import ru.khripunov.socialnetworktt.dto.SimpMessage;


@RestController
@RequiredArgsConstructor
public class ChatWsController {

    @MessageMapping("/{id}/send")
    @SendTo(WebSocketConfig.SIMPLE_BROKER_DESTINATION + "/{id}")
    public SimpMessage processMessage(@DestinationVariable String id, SimpMessage message) {
        System.out.println(message.getMessage());
        return message;
    }

}
