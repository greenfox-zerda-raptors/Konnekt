package com.greenfoxacademy.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.greenfoxacademy.bodies.ContactBody;
import lombok.*;

import java.util.Set;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;

/**
 * Created by Jade Team on 2017.01.24..
 */

@Entity
@Table(name = "`contact`")
@Setter
@JsonSerialize
@NoArgsConstructor
//@ToString
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "contact_id_seq")
    @SequenceGenerator(name = "contact_id_seq",
            sequenceName = "contact_id_seq",
            allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @Getter
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @JsonManagedReference
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Getter
    private User user;
    @Column(name = "contact_name")
    @Getter
    private String name;
    @Column(name = "contact_description")
    @Getter
    private String description;

}
