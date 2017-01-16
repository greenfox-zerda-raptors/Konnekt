package com.greenfoxacademy.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by JadeTeam on 1/16/2017. ControllerTest v0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeControllerTest {

    private HomeController homeController;

    @Before
    public void setup(){
        homeController = new HomeController();
    }

    @Test
    public void homeTest() {
        assertEquals("hello", homeController.home());
    }

}
