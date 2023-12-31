package ru.khripunov.socialnetworktt.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;


import org.springframework.web.socket.messaging.WebSocketStompClient;


import ru.khripunov.socialnetworktt.exception.MyException;
import ru.khripunov.socialnetworktt.handler.MyStompSessionHandler;
import ru.khripunov.socialnetworktt.configuration.WebSocketConfig;
import ru.khripunov.socialnetworktt.dto.SimpMessage;
import ru.khripunov.socialnetworktt.model.Chat;
import ru.khripunov.socialnetworktt.service.ChatService.ChatServiceImpl;
import ru.khripunov.socialnetworktt.service.PeopleService.PeopleServiceImpl;
import ru.khripunov.socialnetworktt.service.TokenService.TokenServiceImpl;



import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Chat", description = "The Chat API.")
public class ChatController {

    private final ChatServiceImpl chatServiceImpl;
    private final PeopleServiceImpl peopleServiceImpl;
    private final TokenServiceImpl tokenServiceImpl;

    private final HashMap<String, StompSession> sessions = new HashMap<>();

    @PostMapping("/chats/{username}")
    @Operation(summary = "Send Message", description = "Send message to {username}.")
    public ResponseEntity<?> sendMessage(@PathVariable String username, @RequestBody String message, @AuthenticationPrincipal Jwt principal) throws ExecutionException, InterruptedException, TimeoutException {

        if(principal!=null && tokenServiceImpl.checkCorrectJwt(principal.getTokenValue())) {

            Chat chat = chatServiceImpl.checkChat(username, message, principal);

            String key = principal.getSubject() + username;

            if (!sessions.containsKey(key)) {

                WebSocketClient client = new StandardWebSocketClient();
                WebSocketStompClient stompClient = new WebSocketStompClient(client);

                stompClient.setMessageConverter(new MappingJackson2MessageConverter());


                StompSessionHandler sessionHandler = new MyStompSessionHandler(chat.getId(), message);
                StompSession stompSession = stompClient.connectAsync("ws://localhost:8080" + WebSocketConfig.END_POINT, sessionHandler).get(1, TimeUnit.SECONDS);


                sessions.put(key, stompSession);


            } else {
                sessions.get(key).send(WebSocketConfig.APP_DESTINATION_PREFIX + "/" + Long.toString(chat.getId()) + "/send", new SimpMessage(message));
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/chats/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Check History Of Chat", description = "Check history of chat with {username}.")
    public ArrayList<ArrayList<String>> checkHistoryOfChat(@PathVariable String username, @AuthenticationPrincipal Jwt principal) throws MyException {
        if(principal!=null && tokenServiceImpl.checkCorrectJwt(principal.getTokenValue())) {
            return chatServiceImpl.checkHistoryOfChat(username, principal);
        }
        throw new MyException("Invalid Token");

    }




}
