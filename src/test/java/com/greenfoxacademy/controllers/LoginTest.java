package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Jade Team on 2017.01.30..
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KonnektApplication.class)
@WebAppConfiguration
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
public class LoginTest {

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void testLoginWithValidCreditentials() throws Exception {
        TestLogin validTestLogin = new TestLogin("admin@admin.hu", "admin");
        String validTestJson = createTestJson(validTestLogin);
        mockMvc.perform(post("/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testLoginWithBadPassword() throws Exception {
        TestLogin validTestLogin = new TestLogin("admin@admin.hu", "12345");
        String validTestJson = createTestJson(validTestLogin);
        mockMvc.perform(post("/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isUnauthorized());
    }

    private String createTestJson(TestLogin testLogin) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(testLogin);
    }
}
