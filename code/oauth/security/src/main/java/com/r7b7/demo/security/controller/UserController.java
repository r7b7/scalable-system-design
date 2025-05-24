package com.r7b7.demo.security.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/home")
    public String login(@AuthenticationPrincipal OidcUser user) {
        return "Hello, " + user.getFullName() + "! Your email is " + user.getEmail();
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser user) {
        return "Login to Continue ";
    }
}
