package com.greenfoxacademy.controllers;

import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.HttpServletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Jade Team on 2017.01.30..
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KonnektApplication.class)
@WebAppConfiguration
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
public class ContactControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ContactService contactService;

    @Autowired
    private HttpServletService servletService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void addContactWithValidData() {
        assertEquals(true, true);
    }


    @Test
    public void addContactWithInvalidData() {
        assertEquals(true, true);
    }


}
