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
    final int DEFAULT_TIMEOUT = 30*60*1000;

    public GenericToken() {
        long current_time=System.currentTimeMillis();
        this.timestamp=new Date(current_time);
        this.valid=new Date(current_time+DEFAULT_TIMEOUT);
    }

    public GenericToken(int minutes) {
        long current_time=System.currentTimeMillis();
        this.timestamp=new Date(current_time);
        this.valid=new Date(current_time+minutes);
    }

    public GenericToken(User user, int minutes) {
        this.user=user;
        long current_time=System.currentTimeMillis();
        this.timestamp=new Date(current_time);
        this.valid=new Date(current_time+minutes);
    }

    public GenericToken(String token, User user) {
        this.user = user;
        this.token = token;
        long current_time=System.currentTimeMillis();
        this.timestamp=new Date(current_time);
        this.valid=new Date(current_time+DEFAULT_TIMEOUT);
    }

    public GenericToken(String token, User user, int minutes) {
        this.user = user;
        this.token = token;
        long current_time=System.currentTimeMillis();
        this.timestamp=new Date(current_time);
        this.valid=new Date(current_time+minutes);
    }
}
