package com.greenfoxacademy.controllers;

import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.responses.NoEmailRegisteredResponse;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by posam on 2017-02-01.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class ForgotPasswordController {

    private ForgotPasswordService forgotPasswordService;
    private UserService userService;
    private SessionService sessionService;


    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService, UserService userService, SessionService sessionService) {
        this.forgotPasswordService = forgotPasswordService;
        this.userService=userService;
        this.sessionService=sessionService;
    }


    @GetMapping("/forgotpassword")
    @ResponseBody
    public ResponseEntity sendMailForNewPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        return (userService.userExists(forgotPasswordRequest.getEmail())) ? generateForgotPasswordSuccess(forgotPasswordRequest.getEmail()) :
                generateForgotPasswordError();
    }

    private ResponseEntity generateForgotPasswordSuccess(String email) {
        String token = forgotPasswordService.generateToken();
        forgotPasswordService.saveToken(token);

        return new ResponseEntity<>("Email sent.", sessionService.generateHeaders(), HttpStatus.ACCEPTED);

    }


    private ResponseEntity generateForgotPasswordError(){
        NoEmailRegisteredResponse noEmailRegisteredResponse= new NoEmailRegisteredResponse(userService);

        return new ResponseEntity<>(noEmailRegisteredResponse, sessionService.generateHeaders(), HttpStatus.BAD_REQUEST);

    }
}
