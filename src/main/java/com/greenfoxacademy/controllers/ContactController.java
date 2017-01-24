package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.HttpServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Jade Team on 2017.01.24..
 */

@RestController
public class ContactController {

    private ContactService contactService;
    private HttpServletService servletService;

    @Autowired
    public ContactController(ContactService contactService, HttpServletService servletService) {
        this.contactService = contactService;
        this.servletService = servletService;
    }

    @PostMapping("/add")
    public ResponseEntity addNewContact(@RequestBody String newContactData) throws Exception {
        JsonNode newContactJson = new ObjectMapper().readValue(newContactData, JsonNode.class);
        Contact newContact = contactService.createNewContact(newContactJson);
        if (contactService.newContactIsValid(newContact)) {
            contactService.saveNewContact(newContact);
            return servletService.createResponseEntity("contact created", "success", HttpStatus.CREATED);
        } else {
            return servletService.createResponseEntity("cannot create contact", "error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/allcontacts")
    public String listAllContact() {
        List<Contact> allContacts = contactService.obtainAllContacts();
        Gson contactsGson = new Gson();
        Type type = new TypeToken<List<Contact>>() {
        }.getType();
        return contactsGson.toJson(allContacts, type);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteContact(@PathVariable Long id) {
        if (contactService.contactBelongsToUser(id)) {
            contactService.deleteContact(id);
            return servletService.createResponseEntity("contact deleted", "success", HttpStatus.I_AM_A_TEAPOT);
        } else {
            return servletService.createResponseEntity("cannot delete contact", "error", HttpStatus.BAD_REQUEST);
        }
    }

}
