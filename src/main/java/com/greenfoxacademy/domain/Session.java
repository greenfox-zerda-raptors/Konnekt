package com.greenfoxacademy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by BSoptei on 1/31/2017.
 */
@Entity
@Table(name = "`session`")
@Data
@AllArgsConstructor
public class Session {
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
    private final int DEFAULT_TIMEOUT = 30;


    public Session() {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }


    public Session(int minutes) {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public Session(User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public Session(String token, User user) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }

    public Session(String token, User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }
}
