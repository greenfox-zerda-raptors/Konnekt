package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class ForgotPasswordService {

    UserService userService;
    ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    public ForgotPasswordService(UserService userService, ForgotPasswordRepository forgotPasswordRepository) {
        this.userService = userService;
        this.forgotPasswordRepository=forgotPasswordRepository;
    }

    private SecureRandom random = new SecureRandom();

    private Email from = new Email("konnekt@heroku.com");


    public String sendTestEmailAndAlsoReturnTheResponsePls(String toAddress) {
        String subject = "Hello World from the SendGrid Java Library!";
        Email to = new Email(toAddress);
        Content content = new Content("text/html", "");
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
        return response.statusCode + "\r\n" + response.body + "\r\n" + response.headers;
    }

    public String generateToken() {
        return new BigInteger(130, random).toString(32);
    }

    public String saveToken(String token) {
        forgotPasswordRepository.save(new ForgotPasswordToken(token));
        return token;
    }

}