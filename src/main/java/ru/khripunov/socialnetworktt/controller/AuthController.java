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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import ru.khripunov.socialnetworktt.dto.Login;
import ru.khripunov.socialnetworktt.dto.PersonForm;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.service.PeopleService;
import ru.khripunov.socialnetworktt.service.TokenService;

import java.time.Instant;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@DependsOn("securityFilterChain")
@Tag(name = "Authentication/Registration", description = "The Authentication/Registration API.")
public class AuthController {
    private final PeopleService peopleService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;

    @Operation(summary = "Confirm Code", description = "Confirm code to registration.")
    @GetMapping("/activate/{code}")
    public ResponseEntity<?> activate(@PathVariable String code){
        if(peopleService.activatePerson(code)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid code", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "User Registration", description = "Registration the user.")
    @PostMapping( "/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody PersonForm personForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
        }

        return peopleService.checkUserUniqueParameters(personForm);

    }

    @Operation(summary = "User Authentication", description = "Authenticate the user and return a JWT token if the user is valid.")
    @PostMapping( "/login")
    public ResponseEntity<?> entryUser(@RequestBody Login login, HttpServletRequest request, HttpServletResponse response){

        if (request.getUserPrincipal() != null) {
            return new ResponseEntity<>("Logout first", HttpStatus.BAD_REQUEST);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPwd()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        try{
            request.login(login.getUsername(), login.getPwd());
        } catch (ServletException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.BAD_REQUEST);
        }

        return peopleService.loginUser(request, response, rememberMeServices);

    }

    @Operation(summary = "User Logout", description = "Logout the user and make invalid JWT token.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) throws ServletException {

        if (principal!=null){
            tokenService.save(principal.getTokenValue(), principal.getClaims().get("exp").toString());
        }
        request.logout();

        return new ResponseEntity<>(HttpStatus.OK);
    }

}