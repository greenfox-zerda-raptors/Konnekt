package com.greenfoxacademy.controllers;

import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.TagService;
import com.greenfoxacademy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
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

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by BSoptei on 2/9/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
@Transactional
public class TagControllerTest extends AbstractJUnit4SpringContextTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TagService tagService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(context).build();
        Session session = new Session("abcde", userService.findUserById(1L));
        sessionService.saveSession(session);
    }

    @Test
    public void instantiateTagControllerTest() {
        TagController tagController = new TagController(null, null);
        assertTrue(tagController != null);
    }

    @Test
    public void getRequestTagsWithValidCredentials() throws Exception {
        Tag tag = new Tag("#newTag");
        tagService.saveTag(tag);
        mockMvc.perform(get("/tags")
                .header("session_token", "abcde"))
                .andExpect(status().isOk());
    }

    @Test
    public void getRequestTagsWithInvalidCredentials() throws Exception {
        mockMvc.perform(get("/tags")
                .header("session_token", "12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getRequestTagsWithoutToken() throws Exception {
        mockMvc.perform(get("/tags"))
                .andExpect(status().isUnauthorized());
    }
}
