package com.greenfoxacademy.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by posam on 2017-02-10.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
@Data
@MappedSuperclass
public abstract class GenericToken {
    @Id
    String token;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;
    @Temporal(value = TemporalType.TIMESTAMP)
    Date timestamp;
    @Temporal(value = TemporalType.TIMESTAMP)
    Date valid;

    @Transient
    final int DEFAULT_TIMEOUT = 30;

    public GenericToken() {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }


    public GenericToken(int minutes) {
        this.timestamp = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public GenericToken(User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }

    public GenericToken(String token, User user) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, DEFAULT_TIMEOUT);
        this.valid = cal.getTime();
    }

    public GenericToken(String token, User user, int minutes) {
        this.timestamp = new Date();
        this.user = user;
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.timestamp);
        cal.add(Calendar.MINUTE, minutes);
        this.valid = cal.getTime();
    }
}
