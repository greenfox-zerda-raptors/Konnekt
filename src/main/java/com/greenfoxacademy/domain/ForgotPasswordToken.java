package com.greenfoxacademy.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Viktor on 2017.02.06..
 */
@Entity
@Table(name = "`password_token`")
@Data
public class ForgotPasswordToken {

    public ForgotPasswordToken() {
    }

    public ForgotPasswordToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    @Id
    private String token;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}
