package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.repository.TagRepository;
import com.greenfoxacademy.responses.TagsResponse;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by BSoptei on 2/8/2017.
 */
@BaseController
public class TagController {

    private TagService tagService;
    private SessionService sessionService;

    @Autowired
    public TagController(TagService tagService,
                         SessionService sessionService) {
        this.tagService = tagService;
        this.sessionService = sessionService;
    }

    @GetMapping("/tags")
    public ResponseEntity listTag() {
        List<Tag> allTags = tagService.findAllTags();
        TagsResponse tagsResponse = new TagsResponse();
        for (Tag tag : allTags) {
            tagsResponse.getTags().add(tag.getTagName());
        }
        return new ResponseEntity<>(tagsResponse,
                                    sessionService.generateHeaders(),
                                    HttpStatus.OK);
    }

}
