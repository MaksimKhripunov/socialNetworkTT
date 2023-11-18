package ru.khripunov.socialnetworktt.service.PeopleService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.khripunov.socialnetworktt.dto.EditProfileRequest;
import ru.khripunov.socialnetworktt.dto.PersonDTO;

import ru.khripunov.socialnetworktt.repository.PersonRepository;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.service.MessageService.MailSender;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements UserDetailsService, PeopleService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    private final JwtEncoder encoder;

    private Person personReg=null;
    private String code;


    @Override
    public Optional<Person> findById(Long id){
        return personRepository.findById(id);
    }


    @Override
    public void updateDate(EditProfileRequest editProfileRequest, BindingResult bindingResult, Jwt principal){

        String username = principal.getSubject();

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if(checkValid("firstname", fieldErrors))
            person.setFirstname(editProfileRequest.getFirstname());

        if(checkValid("lastname", fieldErrors))
            person.setLastname(editProfileRequest.getLastname());

        if(checkValid("username", fieldErrors))
            person.setUsername(editProfileRequest.getUsername());

        if(checkValid("email", fieldErrors)){
            person.setEmail(editProfileRequest.getEmail());
            sendMail(person);
        }else {
            personRepository.save(person);
        }


    }
    @Override
    public void updatePassword(String password, Jwt principal) {

        String username = principal.getSubject();

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        if(password.length()<5)
            return;

        person.setPwd(passwordEncoder.encode(password));

        personRepository.save(person);


    }


    @Override
    public void deleteByUsername(Jwt principal){
        String username = principal.getSubject();
        personRepository.deleteByUsername(username);
    }

    @Override
    public Optional<Person> loadUserByEmail(String email) throws UsernameNotFoundException {
        return personRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

    }

    @Override
    public void save(PersonDTO personDTO){
        Person person = new Person();
        person.setPwd(personDTO.getPwd());
        person.setEmail(personDTO.getEmail());
        person.setFirstname(personDTO.getFirstname());
        person.setLastname(personDTO.getLastname());
        person.setUsername(personDTO.getUsername());

        sendMail(person);

        person.setPwd(passwordEncoder.encode(person.getPassword()));
        personReg=person;
    }

    @Override
    public boolean activatePerson(String code) {
        if(personReg!=null && Objects.equals(this.code, code)){
            personRepository.save(personReg);
            personReg=null;
            this.code="";
            return true;
        }
        return false;
    }


    @Override
    public void switchLimitUsersToMessage(Jwt principal) {

        String username = principal.getSubject();

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        person.setMessageOnlyFriends(!person.getMessageOnlyFriends());

        personRepository.save(person);


    }

    @Override
    public void switchHideFriendsList(Jwt principal) {

        String username = principal.getSubject();

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        person.setHideFriendsList(!person.getHideFriendsList());

        personRepository.save(person);


    }

    @Override
    public ResponseEntity<?> checkUserUniqueParameters(PersonDTO personDTO) {
        try {
            loadUserByUsername(personDTO.getUsername());
            return new ResponseEntity<>("Username already registered", HttpStatus.BAD_REQUEST);
        }catch (UsernameNotFoundException e){
            if (loadUserByEmail(personDTO.getEmail()).isPresent()){
                return new ResponseEntity<>("Email already registered", HttpStatus.BAD_REQUEST);
            }
        }
        save(personDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> loginUser(HttpServletRequest request, HttpServletResponse response, RememberMeServices rememberMeServices) {
        Authentication auth=(Authentication) request.getUserPrincipal();
        Person user = (Person) auth.getPrincipal();
        Person person = (Person) loadUserByUsername(user.getUsername());
        person.setDeleteTime(null);

        String token = generateToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-AUTH-TOKEN", token);

        log.info("User {} logged in.", user.getUsername());
        rememberMeServices.loginSuccess(request, response, auth);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_JSON).body("{\"token\":\"" + token + "\"}");
    }

    private void sendMail(Person person){
        code = UUID.randomUUID().toString();

        String message = String.format(
                "Hello, %s! \n" +
                        "Please, visit next link for activate: http://localhost:8080/api/activate/%s",
                person.getUsername(),
                code
        );
        mailSender.send(person.getEmail(), "Activation code", message);
    }

    private boolean checkValid(String nameOfField, List<FieldError> fieldErrors){
        for(FieldError fieldError : fieldErrors)
        {
            if(Objects.equals(nameOfField, fieldError.getField()))
                return false;
        }
        return true;
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


    /* @Override
    public String checkJWT(Jwt principal){
        String username;

        if(principal != null){
            username=principal.getClaims().get("sub").toString();
            System.out.println(principal);
        }
        else {
            Person user = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            username=user.getUsername();

        }

        return username;
    }*/

}
