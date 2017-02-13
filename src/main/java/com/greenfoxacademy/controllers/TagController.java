package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.responses.AuthCodes;
import com.greenfoxacademy.responses.TagsResponse;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Created by BSoptei on 2/8/2017.
 */
@BaseController
public class TagController extends CommonTasksHandler {

    private TagService tagService;

    @Autowired
    public TagController(TagService tagService,
                         SessionService sessionService) {
        super(sessionService);
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public ResponseEntity listTag(@RequestHeader  HttpHeaders headers) {
        int authResult = authIsSuccessful(headers);
        return (authResult == AuthCodes.OK) ?
                showTags() :
                respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showTags() {
        List<Tag> allTags = tagService.findAllTags();
        TagsResponse tagsResponse = new TagsResponse();
        for (Tag tag : allTags) {
            tagsResponse.getTags().add(tag.getTagName());
        }
        return showCustomResults(tagsResponse, HttpStatus.OK);
    }

}
