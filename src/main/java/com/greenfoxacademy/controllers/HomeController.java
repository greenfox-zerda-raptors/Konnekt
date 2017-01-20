package com.greenfoxacademy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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
