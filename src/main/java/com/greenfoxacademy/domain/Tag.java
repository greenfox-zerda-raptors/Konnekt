package com.greenfoxacademy.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * Created by BSoptei on 2/6/2017.
 */
@Entity
@Table(name = "`tag`")
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "tag_id_seq")
    @SequenceGenerator(name = "tag_id_seq",
            sequenceName = "tag_id_seq",
            allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @Getter
    private Long id;
    @Column(name = "tag_name")
    @Getter
    private String tagName;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Getter
    @JsonBackReference
    private Set<Contact> contacts = new HashSet<>();

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String toString(){
        return tagName;
    }
}
