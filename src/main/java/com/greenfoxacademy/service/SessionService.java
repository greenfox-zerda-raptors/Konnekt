package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
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

    public boolean tokenExists(HttpHeaders headers) {
        return sessionRepository.findOne(headers.get("token").get(0)) != null;
    }

    public Long obtainUserIdFromToken(HttpHeaders headers) {
        return sessionRepository.findOne(headers
                .get("token")
                .get(0))
                .getUser()
                .getId();
    }

    public HttpHeaders generateHeaders(){
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("https://raptor-konnekt.herokuapp.com");
        responseHeaders.setLocation(location);
        return responseHeaders;
    }

    public HttpHeaders generateHeadersWithToken(String token){
        HttpHeaders responseHeadersWithToken = generateHeaders();
        responseHeadersWithToken.set("session_token", token);
        return responseHeadersWithToken;
    }
}