package com.noviq.backend.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String hello() {
         System.out.println("Controller reached");
        return "Authenticated!";
    }

}
