package com.greenfoxacademy.controllers;

import com.greenfoxacademy.constants.AuthCodes;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

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
        this.sessionService = sessionService;
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public ResponseEntity listTag(@RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers, false);
        return (authResult == AuthCodes.OK) ?
                tagService.showTags() :
                sessionService.respondWithNotAuthenticated(authResult);
    }

}
