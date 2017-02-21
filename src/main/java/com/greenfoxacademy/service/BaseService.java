package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.GenericToken;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;
import com.greenfoxacademy.requests.RequestConstants;
import com.greenfoxacademy.responses.AuthCodes;
import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import com.greenfoxacademy.responses.UserRoles;
import com.sendgrid.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by BSoptei on 2/15/2017.
 */
@Service
public class BaseService {
    private SecureRandom random = new SecureRandom();

    private final NotAuthenticatedErrorResponse notAuthResponse =
            new NotAuthenticatedErrorResponse();



    public ResponseEntity respondWithNotAuthenticated(int authResult) {
        notAuthResponse.addErrorMessages(authResult);
        return new ResponseEntity<>(notAuthResponse,
                generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity respondWithBadRequest(Object response) {
        return new ResponseEntity<>(response,
                generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showCustomResults(Object body, HttpStatus status){
        return new ResponseEntity<>(body,
                generateHeaders(),
                status);
    }

    Response generateEmptyResponse() {
        return new Response(400,
                "",
                new HashMap<String, String>() {{
                    put("", "");
                }});
    }


    boolean emailIsValid(String email) { //TODO move these around  maybe?
        return email.contains("@") && email.contains(".");
    }

    public boolean passwordsMatch(AuthRequest request) {
        return request
                .getPassword()
                .equals(request.getPassword_confirmation());
    }

    public int tokenIsValid(String token, Function<String, GenericToken> findFunction, boolean requireAdmin) {
        if (token == null) {
            return AuthCodes.SESSION_TOKEN_NOT_PRESENT;
        } else if (!tokenExists(token, findFunction)) {
            return AuthCodes.SESSION_TOKEN_NOT_REGISTERED;
        } else if (tokenIsExpired(token, findFunction)) {
            return AuthCodes.SESSION_TOKEN_EXPIRED;
        }
        if (requireAdmin) {
            return (obtainUserRoleFromToken(token, findFunction).equals(UserRoles.ADMIN)) ?
                    AuthCodes.OK :
                    AuthCodes.INSUFFICIENT_PRIVILEGES;
        }
        return AuthCodes.OK;
    }

    private boolean tokenIsExpired(String token, Function<String, GenericToken> findFunction) {
        Date currentTime = new Date();
        return (currentTime.after(findFunction.apply(token).getValid()));
    }

    public boolean tokenExists(String token, Function<String, GenericToken> findFunction) {
        return findFunction.apply(token) != null;
    }

    public <T extends GenericToken> T findToken(String token, Function<String, T> findFunction) {
        return findFunction.apply(token);
    }

    public User obtainUserFromToken(String token, Function<String, GenericToken> findFunction) {
        return findFunction.apply(token).getUser();
    }

    public String obtainUserRoleFromToken(String token, Function<String, GenericToken> findFunction) {
        return obtainUserFromToken(token, findFunction).getUserRole();
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

    public String generateToken() {
        return new BigInteger(130, random).toString(32);
    }

    public <T extends BaseRequest> RequestConstants[] validateRequest(T request) {
        RequestConstants[] fieldErrors = new RequestConstants[3];
        if (request instanceof AuthRequest)

    }
}
