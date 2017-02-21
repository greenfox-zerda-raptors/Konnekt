package com.greenfoxacademy.requests;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by BSoptei on 2/1/2017.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthRequest extends BaseRequest{
    private String email;
    private String password;
    private String password_confirmation;
}
