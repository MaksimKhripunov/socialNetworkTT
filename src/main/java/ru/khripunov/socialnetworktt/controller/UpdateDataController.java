package ru.khripunov.socialnetworktt.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.khripunov.socialnetworktt.dto.EditInfo;

import ru.khripunov.socialnetworktt.service.PeopleService;
import ru.khripunov.socialnetworktt.service.TokenService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Update Info", description = "The Update Info API.")
public class UpdateDataController {

    private final PeopleService peopleService;

    private final TokenService tokenService;

    @PatchMapping("/edit")
    @Operation(summary = "Edit Info", description = "Edit username, email, lastname, firstname")
    public ResponseEntity<?> changeInfo(@Valid @RequestBody EditInfo editInfo, BindingResult bindingResult, @AuthenticationPrincipal Jwt principal){

        if(principal==null || tokenService.checkCorrectJwt(principal.getTokenValue())) {
            peopleService.updateDate(editInfo, bindingResult, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/edit/password")
    @Operation(summary = "Change Password", description = "Change password")
    public ResponseEntity<?> changePassword(@RequestParam String password, @AuthenticationPrincipal Jwt principal){

        if(principal==null || tokenService.checkCorrectJwt(principal.getTokenValue())) {
            peopleService.updatePassword(password, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/edit/delete")
    @Operation(summary = "Remove Account", description = "Remove account")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) throws ServletException {
        if(principal==null || tokenService.checkCorrectJwt(principal.getTokenValue())) {
            peopleService.deleteByUsername(principal);
            if (principal!=null){
                tokenService.save(principal.getTokenValue(), principal.getClaims().get("exp").toString());
            }
            request.logout();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/edit/onlyFriends")
    @Operation(summary = "Switch Limit Users To Message", description = "Switch limit users to message")
    public ResponseEntity<?> switchLimitUsersToMessage(@AuthenticationPrincipal Jwt principal){
        if(principal==null || tokenService.checkCorrectJwt(principal.getTokenValue())) {
            peopleService.switchLimitUsersToMessage(principal);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/edit/friendsList")
    @Operation(summary = "Switch Hide Friends List", description = "Switch hide friends list")
    public ResponseEntity<?> switchHideFriendsList(@AuthenticationPrincipal Jwt principal){
        if(principal==null || tokenService.checkCorrectJwt(principal.getTokenValue())) {
            peopleService.switchHideFriendsList(principal);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
