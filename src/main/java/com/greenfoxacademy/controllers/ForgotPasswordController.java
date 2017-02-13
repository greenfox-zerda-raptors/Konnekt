package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by posam on 2017-02-01.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class ForgotPasswordController extends CommonTasksHandler {

    private ForgotPasswordService forgotPasswordService;
    private UserService userService;
    private final String EMAIL_PROBLEM = "There was a problem sending your email, please try again later.";

    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService,
                                    UserService userService,
                                    SessionService sessionService) {
        super(sessionService);
        this.forgotPasswordService = forgotPasswordService;
        this.userService = userService;
    }

    @PostMapping("/forgotpassword")
    @ResponseBody
    public ResponseEntity sendMailForNewPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String usermail = forgotPasswordRequest.getEmail();
        return (userService.userExists(usermail)) ?
                generateForgotPasswordSuccess(userService.findUserByEmail(usermail)) :
                generateForgotPasswordError(createErrorResponse(forgotPasswordRequest));
    }

    @GetMapping("/resetpassword")
    @ResponseBody
    public ResponseEntity getResetPassword(@RequestParam String token) {
        int authResult = forgotPasswordService.tokenIsValid(token);
        if (authResult == AuthCodes.OK) {
            User activeUser = forgotPasswordService.findUserByToken(token);
            return showCustomResults(new UserResponse(activeUser.getId()), HttpStatus.OK);
        } else {
            NotAuthenticatedErrorResponse response =
                    new NotAuthenticatedErrorResponse(forgotPasswordService);
            response.addErrorMessages(authResult);
            return respondWithBadRequest(response);
        }
    }

    @PostMapping("/resetpassword")
    @ResponseBody
    public ResponseEntity resetPassword(@RequestParam String token,
                                        @RequestBody AuthRequest authRequest) {
        int authResult = forgotPasswordService.tokenIsValid(token);
        return (authResult == AuthCodes.OK) ?
            showForgotPasswordResults(authRequest, token):
            respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showForgotPasswordResults(AuthRequest authRequest,
                                                     String token) {
        return (userService.passwordsMatch(authRequest)) ?
            showOKForgotPasswordResults(authRequest, token):
            respondWithBadRequest(createErrorResponse(authRequest));
    }

    private ResponseEntity showOKForgotPasswordResults(AuthRequest authRequest,
                                                       String token){
        User activeUser = forgotPasswordService.findUserByToken(token);
        userService.setUserPassword(activeUser, userService.encryptPassword(authRequest.getPassword()));
        forgotPasswordService.deleteToken(token);
        return showCustomResults(new UserResponse(activeUser.getId()), HttpStatus.OK);
    }

    private ResponseEntity generateForgotPasswordSuccess(User user) {
        String token = forgotPasswordService.saveToken(forgotPasswordService.generateToken(), user);
        int responseStatus = forgotPasswordService.sendEmail(forgotPasswordService.findToken(token));
        return (responseStatus == 202) ?
                showCustomResults("Email sent.", HttpStatus.ACCEPTED):
                showCustomResults(EMAIL_PROBLEM, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private PasswordResetErrorResponse createErrorResponse(AuthRequest request) {
        PasswordResetErrorResponse errorResponse =
                new PasswordResetErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

    private PasswordResetErrorResponse createErrorResponse(ForgotPasswordRequest request) {
        PasswordResetErrorResponse errorResponse =
                new PasswordResetErrorResponse(userService);
        errorResponse.addErrorMessagesForForgotRequest(request);
        return errorResponse;
    }

    private ResponseEntity generateForgotPasswordError(PasswordResetErrorResponse passwordResetErrorResponse) {
        return respondWithBadRequest(passwordResetErrorResponse);
    }
}
