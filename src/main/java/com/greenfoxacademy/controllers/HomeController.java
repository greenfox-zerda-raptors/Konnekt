package com.greenfoxacademy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by JadeTeam on 1/16/2017. Controller v0
 */

@Controller
public class HomeController {

    @ResponseBody
    @RequestMapping(value = {"", "/"})
    public String home() {
        return "hello";
    }

}
