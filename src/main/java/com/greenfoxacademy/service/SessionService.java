package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.SessionRepository;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lenovo on 1/31/2017.
 */
@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private SecureRandom random = new SecureRandom();

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User currentUser) {
        Session currentSession = new Session(generateToken(), currentUser);
        saveSession(currentSession);
        return currentSession;
    }

    private String generateToken() {
        return new BigInteger(130, random).toString(32);
    }

    public void saveSession(Session currentSession) {
        sessionRepository.save(currentSession);
    }

    public boolean tokenExists(String token) {
        return sessionRepository.findOne(token) != null;
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

    public boolean sessionIsValid(HttpHeaders headers) {
        String token = headers.getFirst("session_token");
        return  token != null &&
                tokenExists(token);

    }

    public Response generateEmptyResponse(){
        return new Response(400,
                            "",
                            new HashMap<String, String>(){{
                                put("","");
                            }});
    }
}