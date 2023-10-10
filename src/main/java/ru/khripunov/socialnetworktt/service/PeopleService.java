package ru.khripunov.socialnetworktt.service;


import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    private Person personReg=null;
    private String code;
    public List<Person> findAll(){
        return personRepository.findAll();
    }

    public Optional<Person> findById(Long id){
        return personRepository.findById(id);
    }





    @Transactional
    public void updateDate(Registration registration, BindingResult bindingResult){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Person person = personRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username" + user.getUsername() + "not found"));

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();


        if(checkValid("firstname", fieldErrors))
            person.setFirstname(registration.getFirstname());

        if(checkValid("lastname", fieldErrors))
            person.setLastname(registration.getLastname());

        if(checkValid("username", fieldErrors))
            person.setUsername(registration.getUsername());

        if(checkValid("email", fieldErrors)){
            person.setEmail(registration.getEmail());
            sendMail(person);
        }


    }
    public boolean updatePassword(String password) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Person person = personRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username" + user.getUsername() + "not found"));

        if(password.length()<5)
            return false;

        person.setPassword(passwordEncoder.encode(password));

        personRepository.save(person);

        return true;
    }
    public void sendMail(Person person){
        code = UUID.randomUUID().toString();

        String message = String.format(
                "Hello, %s! \n" +
                        "Please, visit next link for activate: http://localhost:8080/api/activate/%s",
                person.getUsername(),
                code
        );
        mailSender.send(person.getEmail(), "Activation code", message);
    }

    public boolean checkValid(String nameOfField, List<FieldError> fieldErrors){
        for(FieldError fieldError : fieldErrors)
        {
            if(Objects.equals(nameOfField, fieldError.getField()))
                return false;
        }
        return true;
    }
    public void save(Registration registration){
        Person person = new Person();
        person.setPassword(registration.getPwd());
        person.setEmail(registration.getEmail());
        person.setFirstname(registration.getFirstname());
        person.setLastname(registration.getLastname());
        person.setUsername(registration.getUsername());

        sendMail(person);

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personReg=person;
    }




    public void deleteByUsername(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        personRepository.deleteByUsername(user.getUsername());
    }

    @Scheduled(initialDelay = 10)
    public void deleteUsers(){
        System.out.println("WORKS");
        personRepository.deleteUsers();
    }
    @Transactional
    public UserDetails findUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

    }

    @Override
    @Transactional
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


    public boolean activatePerson(String code) {
        if(personReg!=null && Objects.equals(this.code, code)){
            personRepository.save(personReg);
            personReg=null;
            this.code="";
            return true;
        }
        return false;
    }


}
