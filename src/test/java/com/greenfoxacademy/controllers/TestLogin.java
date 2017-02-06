package com.greenfoxacademy.controllers;

import lombok.Data;

/**
 * Created by Lenovo on 2/2/2017.
 */
@Data
public class TestLogin {
    private String email;
    private String password;

    public TestLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

