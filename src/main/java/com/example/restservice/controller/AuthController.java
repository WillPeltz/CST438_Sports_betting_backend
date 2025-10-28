package com.example.restservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class AuthController {

    @GetMapping("/auth/github")
    public void start(
            @RequestParam("return_to") String returnTo,
            HttpServletResponse res
    ) throws IOException {

        // Store return_to so /dev/callback can redirect back to Expo
        Cookie cookie = new Cookie("return_to", returnTo);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // Don’t mark Secure for localhost, otherwise cookie won’t persist
        res.addCookie(cookie);

        // Kick off OAuth2 login via Spring’s configured GitHub client
        res.sendRedirect("/oauth2/authorization/github");
    }
}
