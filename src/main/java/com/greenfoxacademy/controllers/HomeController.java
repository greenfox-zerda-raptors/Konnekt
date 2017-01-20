package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by JadeTeam on 1/16/2017. Controller v0
 */

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @RequestMapping(value = {"", "/"})
    public String home() {
        User newArbitraryUser = new User("bela","fuck");
        userService.save(newArbitraryUser);
        return "hello";
    }


}
