package ru.khripunov.socialnetworktt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.khripunov.socialnetworktt.exception.MyException;
import ru.khripunov.socialnetworktt.service.FriendService.FriendServiceImpl;
import ru.khripunov.socialnetworktt.service.TokenService.TokenServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Friend", description = "The Friend API.")
public class FriendController {

    private final FriendServiceImpl friendServiceImpl;
    private final TokenServiceImpl tokenServiceImpl;

    @GetMapping("/friends/add/{username}")
    @Operation(summary = "Add Friend", description = "Add Friend {username}.")
    public ResponseEntity<?> addFriend(@PathVariable String username, @AuthenticationPrincipal Jwt principal){
        if(principal!=null && tokenServiceImpl.checkCorrectJwt(principal.getTokenValue())) {
            friendServiceImpl.sendInvite(username, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Of All My Friends", description = "List of all my friends.")
    public List<String> allFriends(@AuthenticationPrincipal Jwt principal) throws MyException {
        if(principal!=null && tokenServiceImpl.checkCorrectJwt(principal.getTokenValue())) {
            System.out.println(principal);
            return friendServiceImpl.listOfFriends(principal);
        }
        throw new MyException("Invalid Token");
    }

    @GetMapping(value = "/{username}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Of All Friends", description = "List of all {username}'s friends.")
    public List<String> allFriendsOfAnotherUser(@PathVariable String username, @AuthenticationPrincipal Jwt principal) throws MyException {
        if(principal!=null && tokenServiceImpl.checkCorrectJwt(principal.getTokenValue())) {
            return friendServiceImpl.listOfFriends(username);
        }
        throw new MyException("Invalid Token");
    }

}
