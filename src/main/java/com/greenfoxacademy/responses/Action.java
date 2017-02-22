package com.greenfoxacademy.responses;

import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;

/**
 * Created by posam on 2017-02-22.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
@Getter
@Setter
public class Action {

    private final HttpHeaders headers;
    private final User user;
    private int pathVariable;
    ArrayList<Error> errors = new ArrayList<>();

    public enum intent {
        CONTACT, USER, FORGOT
    }


    public Action(HttpHeaders headers, User user) {
        this.headers = headers;
        this.user = user;
    }

    public Action(HttpHeaders headers, int pathVariable, User user) {
        this.headers = headers;
        this.pathVariable = pathVariable;
        this.user = user;
    }

    public Action addErrors(ArrayList<Valid.issues>[] issues) {

        return this;
    }
}
