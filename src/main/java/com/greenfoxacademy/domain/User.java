package com.greenfoxacademy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(schema = "konnekt", name = "user")
@JsonIgnoreProperties({"userPassword", "id", "userRole", "enabled"})
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
    @Column(name = "user_name")
    private String username;
    @Column(name = "user_password")
    private String password;
    private String userRole;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;

    @Transient
    private String passwordConfirmation;

    public User() {
        this.userRole = "USER";
        this.enabled = true;
    }

//    public User(String userName, String userPassword) {
//        this();
//        this.userName = userName;
//        this.userPassword = userPassword;
//    }
}
