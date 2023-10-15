package ru.khripunov.socialnetworktt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class Registration {

    @NotBlank(message = "not empty password")
    @Size(min=5, message = "should be longer than 5")
    private String pwd;

    @NotBlank(message = "not empty firstname")
    private String firstname;

    @NotBlank(message = "not empty lastname")
    private String lastname;

    @NotBlank(message = "not empty username")
    private String username;

    @NotBlank(message = "not empty email")
    @Email
    private String email;







}
