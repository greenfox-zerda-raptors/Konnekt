package com.greenfoxacademy.service;

import com.sendgrid.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailTest {
    private Email from = new Email("app62245770@heroku.com");


    public void sendTest(String toAddress) {
        String subject = "Hello World from the SendGrid Java Library!";
        Email to = new Email(toAddress);
        Content content = new Content("text/plain", "Hello, Email!");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            System.out.println("juj");
            ex.printStackTrace();
        }
    }
}