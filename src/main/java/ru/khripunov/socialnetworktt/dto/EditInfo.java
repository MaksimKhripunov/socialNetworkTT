package ru.khripunov.socialnetworktt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class EditInfo {

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