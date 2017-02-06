package com.greenfoxacademy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Viktor on 2017.02.06..
 */
@Entity
@Table(name = "`passwordToken`")
@Data
public class ForgotPasswordToken {

    public ForgotPasswordToken(String token) {
        this.token = token;
    }

    @Id
        private String token;
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        private User user;
    }
