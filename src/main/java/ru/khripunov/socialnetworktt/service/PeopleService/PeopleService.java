package ru.khripunov.socialnetworktt.service.PeopleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.validation.BindingResult;
import ru.khripunov.socialnetworktt.dto.EditProfileRequest;
import ru.khripunov.socialnetworktt.dto.PersonDTO;
import ru.khripunov.socialnetworktt.model.Person;

import java.util.Optional;

public interface PeopleService extends UserDetailsService {

    Optional<Person> findById(Long id);
    void updateDate(EditProfileRequest editProfileRequest, BindingResult bindingResult, Jwt principal);
    void updatePassword(String password, Jwt principal);
    void deleteByUsername(Jwt principal);
    Optional<Person> loadUserByEmail(String email);
    void save(PersonDTO personDTO);
    boolean activatePerson(String code);
    void switchLimitUsersToMessage(Jwt principal);
    void switchHideFriendsList(Jwt principal);
    ResponseEntity<?> checkUserUniqueParameters(PersonDTO personDTO);
    ResponseEntity<?> loginUser(HttpServletRequest request, HttpServletResponse response, RememberMeServices rememberMeServices);

    /*String checkJWT(Jwt principal);*/

}
