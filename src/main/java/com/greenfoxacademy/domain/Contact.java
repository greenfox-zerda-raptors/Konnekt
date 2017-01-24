package com.greenfoxacademy.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Jade Team on 2017.01.24..
 */

@Entity
@Table(schema = "konnekt", name = "contact")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "contact_id_seq")
    @SequenceGenerator(schema = "konnekt",
            name = "contact_id_seq",
            sequenceName = "contact_id_seq",
            allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    private String contactName;
    private String contactDescription;
}