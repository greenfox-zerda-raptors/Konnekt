package com.greenfoxacademy.controllers;

import com.greenfoxacademy.config.Profiles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by JadeTeam on 1/16/2017. ControllerTest v0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(Profiles.TEST)
public class HomeControllerTest {

    private HomeController homeController;

    @Before
    public void setup() {
        homeController = new HomeController();
    }

    @Test
    public void homeTest() {
        assertEquals(HttpStatus.OK, homeController.home());
    }


}
