package com.greenfoxacademy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.responses.UserAdminResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by JadeTeam on 1/18/2017. User entity for db
 */
@Entity
@Data
@JsonSerialize
@JsonIgnoreProperties({"password", "userRole", "enabled", "firstName", "lastName"})
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq",
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

    public User() {
        this.userRole = "USER";
        this.enabled = true;
        this.username = "";
    }

    public User(String username, String password, boolean enabled, String userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.enabled = enabled;
    }

    public User(UserAdminResponse display) {
        this.email = display.getEmail();
        this.firstName = display.getFirstName();
        this.lastName = display.getLastName();
        this.userRole = display.getUserRole();
        this.enabled = display.isEnabled();
    }

    public String toString() {
        return "{"
                + "\n\"id\":" + id + ","
                + "\n\"name\":" + username + ","
                + "\n\"email\":" + email
                + "\n}";
    }

}