package com.greenfoxacademy.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    public Contact(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

}
