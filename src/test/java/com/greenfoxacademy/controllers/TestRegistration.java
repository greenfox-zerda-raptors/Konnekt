package com.greenfoxacademy.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by BSoptei on 2/9/2017.
 */
@Data
@AllArgsConstructor
public class TestRegistration {
    private String email;
    private String password;
    private String password_confirmation;

}
