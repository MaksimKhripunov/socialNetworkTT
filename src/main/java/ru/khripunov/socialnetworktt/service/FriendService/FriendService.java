package ru.khripunov.socialnetworktt.service.FriendService;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface FriendService {
    void sendInvite(String recipient, Jwt principal);
    List<String> listOfFriends(Jwt principal);
    List<String> listOfFriends(String username);
    boolean checkRelationships(Long personId, Long friendId);
}
