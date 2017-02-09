package com.greenfoxacademy.service;

import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.responses.AuthCodes;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@Service
public class ForgotPasswordService {

    private ForgotPasswordRepository forgotPasswordRepository;
    private Environment env;
    private SessionService sessionService;

    @Autowired
    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository,
                                 Environment env,
                                 SessionService sessionService) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.env = env;
        this.sessionService = sessionService;
    }

    private SecureRandom random = new SecureRandom();

    private Email from = new Email("konnekt@heroku.com");


    public int sendEmail(ForgotPasswordToken forgotPasswordToken) {
        User user = forgotPasswordToken.getUser();
        String token = forgotPasswordToken.getToken();
        Date valid = forgotPasswordToken.getValid();
        DateFormatter dateFormatter = new DateFormatter();
        dateFormatter.setIso(DateTimeFormat.ISO.DATE_TIME);
        String subject = "Elfelejtett jelszó helyreállítása";
        Email to = new Email(user.getEmail());
        String[] active = env.getActiveProfiles();
        String domain = (Arrays.stream(active).anyMatch(env -> (env.equalsIgnoreCase(Profiles.DEV) || env.equalsIgnoreCase(Profiles.TEST)))) ?
                "http://localhost:8080/resetpassword?token=" :
                "https://raptor-konnekt.herokuapp.com/resetpassword?token="; //TODO make this better
//        boolean test = Arrays.stream(active).anyMatch(env -> env.equalsIgnoreCase(Profiles.TEST));
        String tokenanchor = String.format("<a href=\"%s\">%s</a>", domain + token, domain + token);
        String contentstring = String.format("<!DOCTYPE html><html lang=\"en\"><body>Kedves %s %s, <br><br>egy jelszóhelyreállítási kérés érkezettt hozzánk e-mail címeddel.<br>Ha ez tőled származott, a következő címen állíthatod helyre jelszavad: <br><br>%s<br><br> A link a következő időpontig érvényes: %s. Hogyha ezt nem te indítottad, kérlek jelezd adminisztrátorainknak. <br><br>Üdvözlettel, <br>A Konnekt csapata</body></html>", user.getLastName(), user.getFirstName(), tokenanchor, dateFormatter.print(valid, Locale.getDefault()));
        Content content = new Content("text/html", contentstring);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        System.out.println(System.getenv("SENDGRID_API_KEY"));
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

    public int tokenIsValid(String token) {
        if (token == null) {
            return AuthCodes.SESSION_TOKEN_NOT_PRESENT;
        } else if (!tokenExists(token)) {
            return AuthCodes.SESSION_TOKEN_NOT_REGISTERED;
        } else if (!tokenIsNotExpired(token)) {
            return AuthCodes.SESSION_TOKEN_EXPIRED;
        }
        return AuthCodes.OK;
    }

    boolean tokenExists(String token) {
        return findToken(token) != null;
    }

    public ForgotPasswordToken findToken(String token) {
        return forgotPasswordRepository.findOne(token);
    }

    private boolean tokenIsNotExpired(String token) {
        Date currentTime = new Date();
        return (currentTime.before(forgotPasswordRepository.findOne(token).getValid()));
    }


    public User findUserByToken(String token) {
        return forgotPasswordRepository.findOne(token).getUser();
    }

    public void deleteToken(String token) {
        forgotPasswordRepository.delete(token);
    }
}