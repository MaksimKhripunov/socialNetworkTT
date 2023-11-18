package ru.khripunov.socialnetworktt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.khripunov.socialnetworktt.dto.LoginRequest;
import ru.khripunov.socialnetworktt.dto.PersonDTO;
import ru.khripunov.socialnetworktt.service.PeopleService.PeopleServiceImpl;
import ru.khripunov.socialnetworktt.service.TokenService.TokenServiceImpl;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@DependsOn("securityFilterChain")
@Tag(name = "Authentication/Registration", description = "The Authentication/Registration API.")
public class AuthController {
    private final PeopleServiceImpl peopleServiceImpl;
    private final TokenServiceImpl tokenServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;

    @Operation(summary = "Confirm Code", description = "Confirm code to registration.")
    @GetMapping("/activate/{code}")
    public ResponseEntity<?> activate(@PathVariable String code){
        if(peopleServiceImpl.activatePerson(code)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid code", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "User Registration", description = "Registration the user.")
    @PostMapping( "/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody PersonDTO personDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
        }

        return peopleServiceImpl.checkUserUniqueParameters(personDTO);

    }

    @Operation(summary = "User Authentication", description = "Authenticate the user and return a JWT token if the user is valid.")
    @PostMapping( "/login")
    public ResponseEntity<?> entryUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response){

        if (request.getUserPrincipal() != null) {
            return new ResponseEntity<>("Logout first", HttpStatus.BAD_REQUEST);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPwd()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        try{
            request.login(loginRequest.getUsername(), loginRequest.getPwd());
        } catch (ServletException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.BAD_REQUEST);
        }

        return peopleServiceImpl.loginUser(request, response, rememberMeServices);

    }

    @Operation(summary = "User Logout", description = "Logout the user and make invalid JWT token.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) throws ServletException {

        if (principal!=null){
            tokenServiceImpl.save(principal.getTokenValue(), principal.getClaims().get("exp").toString());
        }
        request.logout();

        return new ResponseEntity<>(HttpStatus.OK);
    }

}