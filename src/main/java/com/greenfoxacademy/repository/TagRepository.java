package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by BSoptei on 2/6/2017.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "select t.tagName from Tag t where t.tagName like :pattern")
    List<Tag> findByBeginningLike(@Param("pattern") String pattern);

    @Query(value = "select t.tagName from Tag t where t.tagName in :tags")
    List<Tag> findByTagNameIn(@Param("tags") String[] tags);

    Tag findByTagName(String tagName);

}
