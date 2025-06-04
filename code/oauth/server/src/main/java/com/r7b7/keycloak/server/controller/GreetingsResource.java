package com.r7b7.keycloak.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsResource {
    
    @GetMapping("/api/greeting")
    public String getGreetings(){
        return "Hello";
    }

    @GetMapping("/test/greeting")
    public String getTestGreetings(){
        return "Hello test";
    }
}
