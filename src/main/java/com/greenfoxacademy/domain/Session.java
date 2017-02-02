package com.greenfoxacademy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by BSoptei on 1/31/2017.
 */
@Entity
@Table(schema = "konnekt", name = "session")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    private String token;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}