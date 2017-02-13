package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.service.CommonTasksService;
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
    private SessionService sessionService;
    private ForgotPasswordRepository forgotPasswordRepository;
    private UserService userService;
    private CommonTasksService commonTasksService;


    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService,
                                    ForgotPasswordRepository forgotPasswordRepository,
                                    UserService userService,
                                    SessionService sessionService,
                                    CommonTasksService commonTasksService) {
        this.forgotPasswordService = forgotPasswordService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userService = userService;
        this.sessionService = sessionService;
        this.commonTasksService = commonTasksService;
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity sendMailForNewPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String usermail = forgotPasswordRequest.getEmail();
        return (userService.userExists(usermail)) ?
                forgotPasswordService.generateForgotPasswordSuccess(userService.findUserByEmail(usermail)) :
                forgotPasswordService.generateForgotPasswordError(forgotPasswordService.createErrorResponse(forgotPasswordRequest));
    }

    @GetMapping("/resetpassword")
    public ResponseEntity getResetPassword(@RequestParam String token) {
        int authResult = sessionService.sessionTokenIsValid(token, forgotPasswordRepository);
        if (authResult == AuthCodes.OK) {
            User activeUser = forgotPasswordService.findUserByToken(token);
            return commonTasksService.showCustomResults(new UserResponse(activeUser.getId()), HttpStatus.OK);
        } else {
            NotAuthenticatedErrorResponse response =
                    new NotAuthenticatedErrorResponse(forgotPasswordService);
            response.addErrorMessages(authResult);
            return commonTasksService.respondWithBadRequest(response);
        }
    }

    @PostMapping("/resetpassword")
    public ResponseEntity resetPassword(@RequestParam String token,
                                        @RequestBody AuthRequest authRequest) {
        int authResult = sessionService.sessionTokenIsValid(token, forgotPasswordRepository);
        return (authResult == AuthCodes.OK) ?
                forgotPasswordService.showForgotPasswordResults(authRequest, token):
                commonTasksService.respondWithNotAuthenticated(authResult);
    }

}
