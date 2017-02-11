package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.responses.Error;
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
public class ForgotPasswordController {

    private ForgotPasswordService forgotPasswordService;
    private ForgotPasswordRepository forgotPasswordRepository;
    private UserService userService;
    private SessionService sessionService;


    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService,
                                    ForgotPasswordRepository forgotPasswordRepository,
                                    UserService userService,
                                    SessionService sessionService) {
        this.forgotPasswordService = forgotPasswordService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userService = userService;
        this.sessionService = sessionService;
    }


    @PostMapping("/forgotpassword")
    @ResponseBody
    public ResponseEntity sendMailForNewPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String usermail = forgotPasswordRequest.getEmail();
        return (userService.userExists(usermail)) ? generateForgotPasswordSuccess(userService.findUserByEmail(usermail)) :
                generateForgotPasswordError(createErrorResponse(forgotPasswordRequest));
    }

    @GetMapping("/resetpassword")
    @ResponseBody
    public ResponseEntity getResetPassword(@RequestParam String token) {
        int authResult = sessionService.sessionTokenIsValid(token, forgotPasswordRepository);
        if (authResult == AuthCodes.OK) {
            User activeUser = forgotPasswordService.findUserByToken(token);
            return new ResponseEntity<>(new UserResponse(activeUser.getId()),
                    sessionService.generateHeaders(),
                    HttpStatus.OK);
        } else {
            NotAuthenticatedErrorResponse response = new NotAuthenticatedErrorResponse(forgotPasswordService);
            response.addErrorMessages(authResult);
            return new ResponseEntity<>(response,
                    sessionService.generateHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resetpassword")
    @ResponseBody
    public ResponseEntity resetPassword(@RequestParam String token, @RequestBody AuthRequest authRequest) {
        int authResult = sessionService.sessionTokenIsValid(token, forgotPasswordRepository);
        if (authResult == AuthCodes.OK) { //TODO clean up this branching statement
            if (userService.passwordsMatch(authRequest)) {
                User activeUser = forgotPasswordService.findUserByToken(token);
                userService.setUserPassword(activeUser, userService.encryptPassword(authRequest.getPassword()));
                forgotPasswordService.deleteToken(token);
                return new ResponseEntity<>(new UserResponse(activeUser.getId()),
                        sessionService.generateHeaders(),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(createErrorResponse(authRequest),
                        sessionService.generateHeaders(),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            NotAuthenticatedErrorResponse notAuthenticatedErrorResponse =
                    new NotAuthenticatedErrorResponse(
                            new Error("Authentication error", "Not authenticated"));
            notAuthenticatedErrorResponse.addErrorMessages(authResult);
            return new ResponseEntity<>(notAuthenticatedErrorResponse,
                    sessionService.generateHeaders(),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity generateForgotPasswordSuccess(User user) {
        String token = forgotPasswordService.saveToken(forgotPasswordService.generateToken(), user);
        int responseStatus = forgotPasswordService.sendEmail(forgotPasswordService.findToken(token));
        return (responseStatus == 202) ? new ResponseEntity<>("Email sent.", sessionService.generateHeaders(), HttpStatus.ACCEPTED) :
                new ResponseEntity<>("There was a problem sending your email, please try again later.", sessionService.generateHeaders(), HttpStatus.SERVICE_UNAVAILABLE);//TODO change these to standard response once spec is in place

    }

    private PasswordResetErrorResponse createErrorResponse(AuthRequest request) { //TODO standardize responses and stop using authrequest (also put that into future spec)
        PasswordResetErrorResponse errorResponse =
                new PasswordResetErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

    private PasswordResetErrorResponse createErrorResponse(ForgotPasswordRequest request) { //TODO unify overloaded methods with lambda or similar
        PasswordResetErrorResponse errorResponse =
                new PasswordResetErrorResponse(userService);
        errorResponse.addErrorMessagesForForgotRequest(request);
        return errorResponse;
    }

    private ResponseEntity generateForgotPasswordError(PasswordResetErrorResponse passwordResetErrorResponse) {
        return new ResponseEntity<>(passwordResetErrorResponse,
                sessionService.generateHeaders(),
                HttpStatus.BAD_REQUEST);

    }
}
