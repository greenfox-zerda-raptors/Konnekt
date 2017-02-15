package com.greenfoxacademy.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * Created by Jade Team on 1/30/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@CrossOrigin(origins = {"https://lasers-cornubite-konnekt.herokuapp.com",
                        "http://localhost:8080",
                        "http://localhost:3000"},
        methods = {RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS},
        exposedHeaders="session_token",
        allowedHeaders = "session_token")
public @interface BaseController {

}
