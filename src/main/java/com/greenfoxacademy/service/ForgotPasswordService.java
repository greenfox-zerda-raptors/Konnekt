package com.greenfoxacademy.service;

import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.responses.ErrorResponse;
import com.greenfoxacademy.responses.UserResponse;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public String saveToken(String token, User user) {
        forgotPasswordRepository.save(new ForgotPasswordToken(token, user));
        return token;
    }

    public ForgotPasswordToken findToken(String token) {
        return forgotPasswordRepository.findOne(token);
    }

    public User findUserByToken(String token) {
        return forgotPasswordRepository.findOne(token).getUser();
    }

    public void deleteToken(String token) {
        forgotPasswordRepository.delete(token);
    }

    public ResponseEntity showForgotPasswordResults(AuthRequest authRequest,
                                                    String token) {
        ArrayList<Valid.issues>[] valid = userService.validateAuthRequest(authRequest, Valid.reset);
        return (userService.authRequestIsValid(valid)) ?
                showOKForgotPasswordResults(authRequest, token) :
                respondWithBadRequest(createErrorResponse(valid));
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
                showCustomResults("Email sent.", HttpStatus.ACCEPTED) :
                showCustomResults(EMAIL_PROBLEM, HttpStatus.SERVICE_UNAVAILABLE);
    }
    public ResponseEntity generateForgotPasswordError(ArrayList<Valid.issues>[] issues) {
        return respondWithBadRequest(createErrorResponse(issues));
    }

    private ErrorResponse createErrorResponse(ArrayList<Valid.issues>[] issues) {
        ErrorResponse errorResponse = new ErrorResponse();
        return errorResponse.addErrorMessages(issues, ErrorResponse.AuthType.RESET);
    }

    public int sessionTokenIsValid(String token, boolean requireAdmin) {
        return sessionService.sessionTokenIsValid(token, forgotPasswordRepository::findOne, requireAdmin);
    }

}