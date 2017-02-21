package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.GenericToken;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.responses.Error;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by Lenovo on 1/31/2017.
 */
@Service
public class SessionService extends BaseService {
    private final SessionRepository sessionRepository;
    private UserService userService;

    @Autowired
    public SessionService(SessionRepository sessionRepository,
                          UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    public Session createSession(User currentUser) {
        Session currentSession = new Session(generateToken(), currentUser);
        saveSession(currentSession);
        return currentSession;
    }

    public void saveSession(Session currentSession) {
        sessionRepository.save(currentSession);
    }

    public Long obtainUserIdFromHeaderToken(HttpHeaders headers) {
        return sessionRepository.findOne(headers
                .getFirst("session_token"))
                .getUser()
                .getId();
    }

    public HttpHeaders generateHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("https://raptor-konnekt.herokuapp.com");
        responseHeaders.setLocation(location);
        return responseHeaders;
    }

    public HttpHeaders generateHeadersWithToken(String token) {
        HttpHeaders responseHeadersWithToken = generateHeaders();
        responseHeadersWithToken.set("session_token", token);
        return responseHeadersWithToken;
    }

    public int sessionIsValid(HttpHeaders headers, boolean requireAdmin) {
        String token = headers.getFirst("session_token");
        return tokenIsValid(token, sessionRepository::findOne, requireAdmin);
    }

    public ResponseEntity generateSuccessfulLogin(AuthRequest request) {
        return showSuccessfulAuthResults(userService.findUserByEmail(request.getEmail()));
    }

    private ResponseEntity showSuccessfulAuthResults(User user) {
        Session currentSession = createSession(user);
        return new ResponseEntity<>(new UserResponse(user.getId()),
                generateHeadersWithToken(currentSession.getToken()),
                HttpStatus.CREATED);
    }

    public ResponseEntity generateLoginError(AuthRequest request) {
        return showCustomResults(crateLoginErrorResponse(request), HttpStatus.UNAUTHORIZED);
    }

    private LoginErrorResponse crateLoginErrorResponse(AuthRequest request) {
        LoginErrorResponse errorResponse =
                new LoginErrorResponse();
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

    public ResponseEntity generateSuccessfulRegistration(AuthRequest request) {
        return showSuccessfulAuthResults(userService.createUser(request));
    }

    public ResponseEntity generateRegistrationError(AuthRequest request) {
        return showCustomResults(createErrorResponse(request), HttpStatus.FORBIDDEN);
    }

    private RegistrationErrorResponse createErrorResponse(AuthRequest request) {
        RegistrationErrorResponse errorResponse =
                new RegistrationErrorResponse();
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

    public ResponseEntity respondWithNotAuthenticated(int authResult) {
        NotAuthenticatedErrorResponse notAuthResponse =
                new NotAuthenticatedErrorResponse(
                        new Error("Authentication error", "Not authenticated"));
        notAuthResponse.addErrorMessages(authResult);
        return new ResponseEntity<>(notAuthResponse,
                generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }
}