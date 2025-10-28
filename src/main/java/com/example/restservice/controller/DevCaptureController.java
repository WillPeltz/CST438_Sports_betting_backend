package com.example.restservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
class DevCaptureController {
  
  @GetMapping("/dev/callback")
  String capture(
    @AuthenticationPrincipal OAuth2User principal,
    @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient
  ) {
    if (principal == null) {
      return "ERROR: Not authenticated. No OAuth2 user found.";
    }
    
    // First, try to get email from user attributes (works if email is public)
    String email = principal.getAttribute("email");
    
    if (email != null) {
      System.out.println("USER EMAIL (from attributes): " + email);
      return "OK – email=" + email;
    }
    
    // If email is null (private), fetch from GitHub's /user/emails endpoint
    String accessToken = authorizedClient.getAccessToken().getTokenValue();
    
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    try {
      ResponseEntity<List> response = restTemplate.exchange(
        "https://api.github.com/user/emails",
        HttpMethod.GET,
        entity,
        List.class
      );
      
      List<Map<String, Object>> emails = response.getBody();
      
      if (emails != null && !emails.isEmpty()) {
        // Find the primary email
        for (Map<String, Object> emailObj : emails) {
          Boolean primary = (Boolean) emailObj.get("primary");
          if (primary != null && primary) {
            email = (String) emailObj.get("email");
            break;
          }
        }
        
        // If no primary found, use the first verified email
        if (email == null) {
          for (Map<String, Object> emailObj : emails) {
            Boolean verified = (Boolean) emailObj.get("verified");
            if (verified != null && verified) {
              email = (String) emailObj.get("email");
              break;
            }
          }
        }
        
        // Last resort: use first email
        if (email == null && !emails.isEmpty()) {
          email = (String) emails.get(0).get("email");
        }
      }
      
      if (email != null) {
        System.out.println("USER EMAIL (from /user/emails): " + email);
        return "OK – email=" + email;
      } else {
        return "ERROR: No emails found in GitHub account";
      }
      
    } catch (Exception e) {
      return "ERROR: Failed to fetch emails from GitHub: " + e.getMessage();
    }
  }
}