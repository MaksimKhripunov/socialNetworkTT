package ru.khripunov.socialnetworktt.service.TokenService;

public interface TokenService {
    void save(String tokenValue, String date);
    boolean checkCorrectJwt(String jwt);
}
