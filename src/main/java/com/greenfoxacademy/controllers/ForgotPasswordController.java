package com.greenfoxacademy.controllers;

import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.constants.AuthCodes;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import com.greenfoxacademy.responses.UserResponse;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

/**
 * Created by posam on 2017-02-01.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class ForgotPasswordController {

    private ForgotPasswordService forgotPasswordService;
    private UserService userService;


    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService,
                                    UserService userService) {
        this.forgotPasswordService = forgotPasswordService;
        this.userService = userService;
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity sendMailForNewPassword(@RequestBody AuthRequest authRequest) {
        ArrayList<Valid.issues>[] valid = userService.validateAuthRequest(authRequest, Valid.forgot);
        String userMail = authRequest.getEmail();
        return (userService.authRequestIsValid(valid)) ?
                forgotPasswordService.generateForgotPasswordSuccess(userService.findUserByEmail(userMail)) :
                forgotPasswordService.generateForgotPasswordError(valid);
    }

    @GetMapping("/resetpassword")
    public ResponseEntity getResetPassword(@RequestParam String token) {
        int authResult = forgotPasswordService.sessionTokenIsValid(token, false);
        if (authResult == AuthCodes.OK) {
            User activeUser = forgotPasswordService.findUserByToken(token);
            return forgotPasswordService.showCustomResults(new UserResponse(activeUser.getId()), HttpStatus.OK);
        } else {
            NotAuthenticatedErrorResponse response =
                    new NotAuthenticatedErrorResponse();
            response.addErrorMessages(authResult);
            return forgotPasswordService.respondWithBadRequest(response);
        }
    }

    @PostMapping("/resetpassword")
    public ResponseEntity resetPassword(@RequestParam String token,
                                        @RequestBody AuthRequest authRequest) {
        int authResult = forgotPasswordService.sessionTokenIsValid(token, false);
        return (authResult == AuthCodes.OK) ?
                forgotPasswordService.showForgotPasswordResults(authRequest, token) :
                forgotPasswordService.respondWithNotAuthenticated(authResult);
    }

}
