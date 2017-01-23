package com.greenfoxacademy.controllers;

import com.greenfoxacademy.config.KonnektAppConfig;
import com.greenfoxacademy.config.SecurityWebAppInitializer;
import com.greenfoxacademy.config.WebSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityWebAppInitializer.class, WebSecurityConfig.class, KonnektAppConfig.class})
@WebAppConfiguration
//@ActiveProfiles("production")
public class RegistrationControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.standaloneSetup(SecurityWebAppInitializer.class, WebSecurityConfig.class, KonnektAppConfig.class).addFilters(springSecurityFilterChain)
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void thatLoginWithCorrectCredentialsWorks() throws Exception {
        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "admin")
                        .param("password", "admin")
                //).andExpect(status().isOk());
        ).andExpect(status().isFound());
    }

//    @Test
//    public void registerGetTest() throws Exception {
//        mockMvc.perform(get("/register").with(user("admin")));
//    }
//
//    @Test
//    public void registerPostTest() {
//    }
}
