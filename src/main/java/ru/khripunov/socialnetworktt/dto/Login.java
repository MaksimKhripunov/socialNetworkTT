package ru.khripunov.socialnetworktt.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login  {
    private String pwd;
    private String username;
}
