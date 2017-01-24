package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by JadeTeam on 1/20/2017. Register new user
 */
@Controller
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    @ResponseBody
    public String registerGet() {
        return "";
    }

    @PostMapping("/register")
    public ResponseEntity registerPost(@RequestBody String regFormData) throws IOException {
        JsonNode registrationJson = new ObjectMapper().readValue(regFormData, JsonNode.class);
        User newUser = userService.createUser(registrationJson);
        String passwordConfirmation = registrationJson.get("passwordConfirmation").textValue();
        if (registrationIsValid(newUser, passwordConfirmation)) {
            userService.save(newUser);
            return createResponseEntity("user created","success", HttpStatus.CREATED);
        } else {
            return createResponseEntity("cannot register user","error", HttpStatus.BAD_REQUEST);
        }
    }

    private HttpHeaders createResponseHeaders(String headerValue) {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("localhost:8080");
        responseHeaders.setLocation(location);
        responseHeaders.set("status", headerValue);
        return responseHeaders;
    }

    private boolean registrationIsValid(User newUser, String passwordConfirmation) {
        return !userService.userExists(newUser.getUserName())
                && passwordConfirmation.equals(newUser.getUserPassword());
    }

    private ResponseEntity createResponseEntity(String response,
                                                String headerValue,
                                                HttpStatus httpStatus){
        Gson responseGson = new Gson();
        return new ResponseEntity<>
                (responseGson.toJson(new CustomResponse(response)),
                createResponseHeaders(headerValue),
                httpStatus);
    }



}
