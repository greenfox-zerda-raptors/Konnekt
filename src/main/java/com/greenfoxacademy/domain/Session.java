package com.greenfoxacademy.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Lenovo on 1/31/2017.
 */
@Entity
@Table(schema = "konnekt", name = "session")
@Data
public class Session {
    @Id
    private String token;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
