package com.greenfoxacademy.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by JadeTeam on 1/18/2017. User entity for db
 */
@Entity
@Data
@Getter
@Setter
@Table(schema = "konnekt", name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "user_id_seq")
    @SequenceGenerator(schema = "konnekt",
            name = "user_id_seq",
            sequenceName = "user_id_seq",
            allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String userName;
    private String userPassword;
    private String userRole;
    private boolean enabled;

    public User() {
        this.userRole = "USER";
        this.enabled = true;
    }

    public User(String userName, String userPassword) {
        this();
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
