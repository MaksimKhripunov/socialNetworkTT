package ru.khripunov.socialnetworktt.service.ChatService;

import org.springframework.security.oauth2.jwt.Jwt;
import ru.khripunov.socialnetworktt.model.Chat;
import ru.khripunov.socialnetworktt.model.Message;
import ru.khripunov.socialnetworktt.model.Person;

import java.util.ArrayList;
import java.util.List;

public interface ChatService {
    List<Message> findByUsername(String username, Jwt principal);
    Chat checkChat(String username, String content, Jwt principal);

    ArrayList<ArrayList<String>> checkHistoryOfChat(String username, Jwt principal);
}
