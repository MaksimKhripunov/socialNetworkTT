package ru.khripunov.socialnetworktt.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.khripunov.socialnetworktt.dto.Registration;

import ru.khripunov.socialnetworktt.service.PeopleService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UpdateDataController {

    private final PeopleService peopleService;

    @PatchMapping("/edit")
    public ResponseEntity<?> changeInfo(@Valid @RequestBody Registration registration, BindingResult bindingResult){
        peopleService.updateDate(registration, bindingResult);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity<?> changePassword(@RequestParam String password){
        peopleService.updatePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) throws ServletException {
        peopleService.deleteByUsername();
        request.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
