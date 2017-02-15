package com.greenfoxacademy.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by BSoptei on 1/31/2017.
 */
@Entity
@Table(name = "`session`")
public class Session extends GenericToken {

    public Session() {
    }

    public Session(int minutes) {
        super(minutes);
    }

    public Session(User user, int minutes) {
        super(user, minutes);
    }

    public Session(String token, User user) {
        super(token, user);
    }

    public Session(String token, User user, int minutes) {
        super(token, user, minutes);
    }
}
