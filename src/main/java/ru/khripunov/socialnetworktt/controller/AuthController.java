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
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.dto.Registration;
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
    private final RememberMeServices rememberMeServices;

    private final JwtEncoder encoder;

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
    public ResponseEntity<?> addUser(@Valid @RequestBody Registration registration, BindingResult bindingResult){
        for(FieldError fieldError : bindingResult.getFieldErrors())
            System.out.println(fieldError.getField());
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
        }else{
            try{
                peopleService.loadUserByUsername(registration.getUsername());
                return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
            }catch (UsernameNotFoundException e){
                    peopleService.save(registration);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    @Operation(summary = "User Authentication", description = "Authenticate the user and return a JWT token if the user is valid.")
    @PostMapping( "/login")
    public ResponseEntity<?> entryUser(@RequestBody Login login, HttpServletRequest request, HttpServletResponse response){

        if (request.getUserPrincipal() != null) {
            throw new RuntimeException("Please logout first.");
        }

        try{
            request.login(login.getUsername(), login.getPwd());
        } catch (ServletException e) {
            throw new RuntimeException("Invalid username/email or password");
        }

        Authentication auth=(Authentication) request.getUserPrincipal();

        User user=(User) auth.getPrincipal();

        Person person = (Person) peopleService.findUserByUsername(user.getUsername());

        person.setDeleteTime(null);



        String token = generateToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-AUTH-TOKEN", token);


        log.info("User {} logged in.", user.getUsername());
        rememberMeServices.loginSuccess(request, response, auth);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_JSON).body("{\"token\":\"" + token + "\"}");

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

    private String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(userDetails.getUsername())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


}