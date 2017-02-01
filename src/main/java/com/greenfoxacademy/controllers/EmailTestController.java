package com.greenfoxacademy.controllers;

import com.greenfoxacademy.service.EmailTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by posam on 2017-02-01.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class EmailTestController {

    EmailTest emailTest;


    @Autowired
    public EmailTestController(EmailTest emailTest) {
        this.emailTest = emailTest;
    }


    @GetMapping("/testmail")
    @ResponseBody
    public String sendTestMail() {
        emailTest.sendTest("posa.marci@gmail.com");

        return "elvileg elment";
    }
}
