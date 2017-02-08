package com.greenfoxacademy.service;

import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class ForgotPasswordService {

    UserService userService;
    ForgotPasswordRepository forgotPasswordRepository;
    Environment env;

    @Autowired
    public ForgotPasswordService(UserService userService, ForgotPasswordRepository forgotPasswordRepository, Environment env) {
        this.userService = userService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.env = env;


    }

    private SecureRandom random = new SecureRandom();

    private Email from = new Email("konnekt@heroku.com");


    public int sendEmail(String token, User user) {
        String subject = "Elfelejtett jelszó helyreállítása";
        Email to = new Email(user.getEmail());
        String[] active = env.getActiveProfiles();
        String domain = (Arrays.stream(active).anyMatch(env -> (env.equalsIgnoreCase(Profiles.DEV) || env.equalsIgnoreCase(Profiles.TEST)))) ?
                "http://localhost:8080/resetpassword?token=" :
                "https://raptor-konnekt.herokuapp.com/resetpassword?token="; //TODO make this better
        String tokenanchor = String.format("<a href=\"%s\">%s</a>", domain + token, domain + token);
        String contentstring = String.format("<!DOCTYPE html><html lang=\"en\"><body>Kedves %s %s, <br><br>egy jelszóhelyreállítási kérés érkezettt hozzánk e-mail címeddel.<br>Ha ez tőled származott, a kóvetkező címen állíthatod helyre jelszavad: <br>%s<br>Hogyha ezt nem te indítottad, kérlek jelezd adminisztrátorainknak. <br><br>Üdvözlettel, <br>A Konnekt csapata</body></html>", user.getLastName(), user.getFirstName(), tokenanchor);
        Content content = new Content("text/html", contentstring);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        Response response = null;
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            System.out.println("juj");
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

    public User findUserbyToken(String token) {
        return forgotPasswordRepository.findOne(token).getUser();
    }

    public void deleteToken(String token) {
        forgotPasswordRepository.delete(token);
    }
}