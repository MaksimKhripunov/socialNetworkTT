package ru.khripunov.socialnetworktt.service;


import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.khripunov.socialnetworktt.dto.EditInfo;
import ru.khripunov.socialnetworktt.dto.Registration;
import ru.khripunov.socialnetworktt.repository.PersonRepository;
import ru.khripunov.socialnetworktt.model.Person;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PeopleService implements UserDetailsService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    private final JwtDecoder decoder;
    private Person personReg=null;
    private String code;
    public List<Person> findAll(){
        return personRepository.findAll();
    }

    public Optional<Person> findById(Long id){
        return personRepository.findById(id);
    }


    public void updateDate(EditInfo editInfo, BindingResult bindingResult, Jwt principal){

        String username = checkJWT(principal);

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if(checkValid("firstname", fieldErrors))
            person.setFirstname(editInfo.getFirstname());

        if(checkValid("lastname", fieldErrors))
            person.setLastname(editInfo.getLastname());

        if(checkValid("username", fieldErrors))
            person.setUsername(editInfo.getUsername());

        if(checkValid("email", fieldErrors)){
            person.setEmail(editInfo.getEmail());
            sendMail(person);
        }else {
            personRepository.save(person);
        }


    }
    public boolean updatePassword(String password, Jwt principal) {

        String username = checkJWT(principal);

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        if(password.length()<5)
            return false;

        person.setPwd(passwordEncoder.encode(password));

        personRepository.save(person);

        return true;
    }


    public void deleteByUsername(Jwt principal){
        String username = checkJWT(principal);
        personRepository.deleteByUsername(username);
    }



    public UserDetails findUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(person.getUsername().toUpperCase()));
        UserDetails user = User.withUsername(person.getUsername())
                .password(person.getPassword())
                .authorities(grantedAuthorities).build();
        return user;
    }

    public void save(Registration registration){
        Person person = new Person();
        person.setPwd(registration.getPwd());
        person.setEmail(registration.getEmail());
        person.setFirstname(registration.getFirstname());
        person.setLastname(registration.getLastname());
        person.setUsername(registration.getUsername());

        sendMail(person);

        person.setPwd(passwordEncoder.encode(person.getPassword()));
        personReg=person;
    }


    public boolean activatePerson(String code) {
        if(personReg!=null && Objects.equals(this.code, code)){
            personRepository.save(personReg);
            personReg=null;
            this.code="";
            return true;
        }
        return false;
    }

    public String checkJWT(Jwt principal){
        String username;

        if(principal != null){
            username=principal.getClaims().get("sub").toString();
        }
        else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            username=user.getUsername();

        }

        return username;
    }


    public void switchLimitUsersToMessage(Jwt principal) {

        String username = checkJWT(principal);

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        person.setMessageOnlyFriends(!person.getMessageOnlyFriends());

        personRepository.save(person);


    }

    public void switchHideFriendsList(Jwt principal) {

        String username = checkJWT(principal);

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

        person.setHideFriendsList(!person.getHideFriendsList());

        personRepository.save(person);


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

}
