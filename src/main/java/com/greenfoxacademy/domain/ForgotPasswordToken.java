package com.greenfoxacademy.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Viktor on 2017.02.06..
 */
@Entity
@Table(name = "`password_token`")
@Data
public class ForgotPasswordToken {

    @Id
    private String token;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timestamp;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date valid;

    @Transient
    public static final int DEFAULT_TIMEOUT = 30;


    public ForgotPasswordToken() {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }


    public ForgotPasswordToken(int minutes) {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public ForgotPasswordToken(User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public ForgotPasswordToken(String token, User user) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }

    public ForgotPasswordToken(String token, User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }


}
