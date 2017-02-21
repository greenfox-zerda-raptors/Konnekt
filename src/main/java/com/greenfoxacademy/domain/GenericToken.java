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
    final int DEFAULT_TIMEOUT_IN_MIN = 30;
    @Transient
    final int MINUTE_IN_MILLISEC = 60000;
    @Transient
    long currentTimeInMillisec;

    public GenericToken() {
        setTimestamp();
        this.valid=new Date(currentTimeInMillisec+DEFAULT_TIMEOUT_IN_MIN*MINUTE_IN_MILLISEC);
    }

    public GenericToken(int minutes) {
        setTimestamp();
        this.valid=new Date(currentTimeInMillisec+minutes*MINUTE_IN_MILLISEC);
    }

    public GenericToken(User user, int minutes) {
        setTimestamp();
        this.user=user;
        this.valid=new Date(currentTimeInMillisec+minutes*MINUTE_IN_MILLISEC);
    }

    public GenericToken(String token, User user) {
        setTimestamp();
        this.user = user;
        this.token = token;
        this.valid=new Date(currentTimeInMillisec+DEFAULT_TIMEOUT_IN_MIN*MINUTE_IN_MILLISEC);
    }

    public GenericToken(String token, User user, int minutes) {
        setTimestamp();
        this.user = user;
        this.token = token;
        this.valid=new Date(currentTimeInMillisec+minutes*MINUTE_IN_MILLISEC);
    }

    public void setTimestamp(){
        this.currentTimeInMillisec=System.currentTimeMillis();
        this.timestamp=new Date(currentTimeInMillisec);
    }
}
