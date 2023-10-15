package ru.khripunov.socialnetworktt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Token;
import ru.khripunov.socialnetworktt.repository.TokenRepository;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    public void save(String tokenValue, String date) {
        Token token = new Token();
        LocalDateTime dateTime = LocalDateTime.parse(date.substring(0,date.length()-1));
        token.setToken(tokenValue);
        token.setDateTime(dateTime);
        tokenRepository.save(token);
    }

    public boolean checkCorrectJwt(String jwt){
        if(tokenRepository.findByToken(jwt).isPresent()){
            return false;
        }
        return true;
    }
}
