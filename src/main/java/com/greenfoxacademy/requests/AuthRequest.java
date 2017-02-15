package com.greenfoxacademy.requests;

import lombok.Data;

/**
 * Created by BSoptei on 2/1/2017.
 */
@Data
public class AuthRequest extends AuthRelatedRequest{
    private String email;
    private String password;
    private String password_confirmation;
}
