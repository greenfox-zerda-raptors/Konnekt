package com.greenfoxacademy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by JadeTeam on 1/16/2017. Controller v0
 */

@BaseController
public class HomeController {

    @RequestMapping(value = {"", "/"})
    public HttpStatus home() {
        return HttpStatus.OK;
    }

}
