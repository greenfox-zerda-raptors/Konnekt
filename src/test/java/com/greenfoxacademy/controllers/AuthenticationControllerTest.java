package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Jade Team on 2017.01.30..
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@WebAppConfiguration
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
@Transactional
public class AuthenticationControllerTest extends AbstractJUnit4SpringContextTests {

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
        Session session = new Session("abcde", userService.findUserById(1L));
        sessionService.saveSession(session);
    }

    @Test
    public void testLoginWithValidCredentials() throws Exception {
        TestLogin validTestLogin = new TestLogin("admin@admin.hu", "admin");
        String validTestJson = createTestJson(validTestLogin);
        mockMvc.perform(post("/login").with(csrf())
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isCreated());
    }


    @Test
    public void testLoginWithBadPassword() throws Exception {
        TestLogin validTestLogin = new TestLogin("admin@admin.hu", "12345");
        String validTestJson = createTestJson(validTestLogin);
        mockMvc.perform(post("/login").with(csrf())
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogout() throws Exception {
        TestLogin validTestLogin = new TestLogin("admin@admin.hu", "admin");
        String validTestJson = createTestJson(validTestLogin);

        mockMvc.perform(post("/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/logout").with(csrf())
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isOk());
    }

    private String createTestJson(TestLogin testLogin) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(testLogin);
    }
}
