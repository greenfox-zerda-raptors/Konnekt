package com.greenfoxacademy.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Lenovo on 2/2/2017.
 */
@Data
@AllArgsConstructor
public class TestLogin {
    private String email;
    private String password;
}

