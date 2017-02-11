package com.greenfoxacademy.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Viktor on 2017.02.06..
 */
@Entity
@Table(name = "`password_token`")
public class ForgotPasswordToken extends GenericToken {

    public ForgotPasswordToken() {
    }

    public ForgotPasswordToken(int minutes) {
        super(minutes);
    }

    public ForgotPasswordToken(User user, int minutes) {
        super(user, minutes);
    }

    public ForgotPasswordToken(String token, User user) {
        super(token, user);
    }

    public ForgotPasswordToken(String token, User user, int minutes) {
        super(token, user, minutes);
    }
}
