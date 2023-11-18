package ru.khripunov.socialnetworktt.service.TokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Token;
import ru.khripunov.socialnetworktt.repository.TokenRepository;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    @Override
    public void save(String tokenValue, String date) {
        Token token = new Token();
        LocalDateTime dateTime = LocalDateTime.parse(date.substring(0,date.length()-1));
        token.setToken(tokenValue);
        token.setDateTime(dateTime);
        tokenRepository.save(token);
    }
    @Override
    public boolean checkCorrectJwt(String jwt){
        if(tokenRepository.findByToken(jwt).isPresent()){
            return false;
        }
        return true;
    }
}
