package com.example.restservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // For this API + OAuth flow, CSRF can be off (revisit if you add forms/cookies)
      .csrf(csrf -> csrf.disable())

      // Let everything through during setup so the OAuth endpoints work cleanly
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/public/**", "/actuator/**", "/dev/callback").permitAll()
        .anyRequest().permitAll()
      )

      .oauth2Login(oauth -> oauth
        .successHandler((req, res, auth) -> {
          String code = req.getParameter("code");
          String state = req.getParameter("state");
          res.sendRedirect("http://localhost:8080/dev/callback?code=" + code + "&state=" + state);
        })
      );

    return http.build();
  }
}
