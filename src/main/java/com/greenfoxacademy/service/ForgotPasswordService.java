package com.greenfoxacademy.service;

import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.responses.PasswordResetErrorResponse;
import com.greenfoxacademy.responses.UserResponse;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Service
public class ForgotPasswordService extends BaseService {

    private ForgotPasswordRepository forgotPasswordRepository;
    private Environment env;
    private SessionService sessionService;
    private UserService userService;
    private final String EMAIL_PROBLEM = "There was a problem sending your email, please try again later.";

    @Autowired
    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository,
                                 Environment env,
                                 SessionService sessionService,
                                 UserService userService) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.env = env;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    private SecureRandom random = new SecureRandom();

    private Email from = new Email("konnekt@heroku.com");


    public int sendEmail(ForgotPasswordToken forgotPasswordToken) {
        User user = forgotPasswordToken.getUser();
        String token = forgotPasswordToken.getToken();
        Date valid = forgotPasswordToken.getValid();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String subject = "Elfelejtett jelszó helyreállítása";
        Email to = new Email(user.getEmail());
        String[] active = env.getActiveProfiles();
        String domain = (Arrays.stream(active).anyMatch(env -> (env.equalsIgnoreCase(Profiles.DEV) || env.equalsIgnoreCase(Profiles.TEST)))) ?
                "http://localhost:8080/resetpassword?token=" :
                "https://raptor-konnekt.herokuapp.com/resetpassword?token="; //TODO is this necessary?
        String tokenanchor = String.format("<a href=\"%s\">%s</a>", domain + token, domain + token);
        String contentstring = String.format("<!DOCTYPE html><html lang=\"en\"><body>Kedves %s %s, <br><br>egy jelszóhelyreállítási kérés érkezettt hozzánk e-mail címeddel.<br>Ha ez tőled származott, a következő címen állíthatod helyre jelszavad: <br><br>%s<br><br> A link a következő időpontig érvényes: %s. Hogyha ezt nem te indítottad, kérlek jelezd adminisztrátorainknak. <br><br>Üdvözlettel, <br>A Konnekt csapata</body></html>", user.getLastName(), user.getFirstName(), tokenanchor, dateFormat.format(valid));
        Content content = new Content("text/html", contentstring);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        Response response = sessionService.generateEmptyResponse();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            response = sg.api(request);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response.statusCode;
    }

    public String generateToken() {
        return new BigInteger(130, random).toString(32);
    }

    public String saveToken(String token, User user) {
        forgotPasswordRepository.save(new ForgotPasswordToken(token, user));
        return token;
    }

    public ForgotPasswordToken findToken(String token) {
        return (ForgotPasswordToken) forgotPasswordRepository.findOne(token);
    }

    public User findUserByToken(String token) {
        return forgotPasswordRepository.findOne(token).getUser();
    }

    public void deleteToken(String token) {
        forgotPasswordRepository.delete(token);
    }


    public ResponseEntity showForgotPasswordResults(AuthRequest authRequest,
                                                    String token) {
        return (userService.passwordsMatch(authRequest)) ?
                showOKForgotPasswordResults(authRequest, token):
                respondWithBadRequest(createErrorResponse(authRequest));
    }

    private ResponseEntity showOKForgotPasswordResults(AuthRequest authRequest,
                                                       String token) {
        User activeUser = findUserByToken(token);
        userService.setUserPassword(activeUser, userService.encryptPassword(authRequest.getPassword()));
        deleteToken(token);
        return showCustomResults(new UserResponse(activeUser.getId()), HttpStatus.OK);
    }

    public ResponseEntity generateForgotPasswordSuccess(User user) {
        String token = saveToken(generateToken(), user);
        int responseStatus = sendEmail(findToken(token));
        return (responseStatus == 202) ?
                showCustomResults("Email sent.", HttpStatus.ACCEPTED):
                showCustomResults(EMAIL_PROBLEM, HttpStatus.SERVICE_UNAVAILABLE);
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

    public ResponseEntity generateForgotPasswordError(ForgotPasswordRequest request) {
        return respondWithBadRequest(createErrorResponse(request));
    }

    public int sessionTokenIsValid(String token) {
        return sessionService.sessionTokenIsValid(token,forgotPasswordRepository::findOne);
    }

}