package com.example.restservice.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  private final ClientRegistrationRepository clientRegistrationRepository;
  
  public AuthController(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }
  
  @PostMapping("/github/exchange")
  public Map<String, Object> exchangeGitHubCode(@RequestBody Map<String, String> request) {
    String code = request.get("code");
    
    if (code == null || code.isEmpty()) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Code is required");
      return error;
    }
    
    try {
      // Get GitHub OAuth configuration from Spring Security
      ClientRegistration github = clientRegistrationRepository.findByRegistrationId("github");
      String clientId = github.getClientId();
      String clientSecret = github.getClientSecret();
      
      // Step 1: Exchange code for access token
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders tokenHeaders = new HttpHeaders();
      tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
      tokenHeaders.set("Accept", "application/json");
      
      Map<String, String> tokenRequest = new HashMap<>();
      tokenRequest.put("client_id", clientId);
      tokenRequest.put("client_secret", clientSecret);
      tokenRequest.put("code", code);
      
      HttpEntity<Map<String, String>> tokenEntity = new HttpEntity<>(tokenRequest, tokenHeaders);
      
      ResponseEntity<Map> tokenResponse = restTemplate.exchange(
        "https://github.com/login/oauth/access_token",
        HttpMethod.POST,
        tokenEntity,
        Map.class
      );
      
      Map<String, Object> tokenBody = tokenResponse.getBody();
      if (tokenBody == null || !tokenBody.containsKey("access_token")) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Failed to get access token from GitHub");
        return error;
      }
      
      String accessToken = (String) tokenBody.get("access_token");
      
      // Step 2: Get user info from GitHub
      HttpHeaders userHeaders = new HttpHeaders();
      userHeaders.setBearerAuth(accessToken);
      HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);
      
      ResponseEntity<Map> userResponse = restTemplate.exchange(
        "https://api.github.com/user",
        HttpMethod.GET,
        userEntity,
        Map.class
      );
      
      Map<String, Object> userData = userResponse.getBody();
      String email = userData != null ? (String) userData.get("email") : null;
      
      // Step 3: If email is null (private), fetch from /user/emails endpoint
      if (email == null) {
        ResponseEntity<List> emailsResponse = restTemplate.exchange(
          "https://api.github.com/user/emails",
          HttpMethod.GET,
          userEntity,
          List.class
        );
        
        List<Map<String, Object>> emails = emailsResponse.getBody();
        
        if (emails != null && !emails.isEmpty()) {
          // Find primary email
          for (Map<String, Object> emailObj : emails) {
            Boolean primary = (Boolean) emailObj.get("primary");
            if (primary != null && primary) {
              email = (String) emailObj.get("email");
              break;
            }
          }
          
          // If no primary, find verified email
          if (email == null) {
            for (Map<String, Object> emailObj : emails) {
              Boolean verified = (Boolean) emailObj.get("verified");
              if (verified != null && verified) {
                email = (String) emailObj.get("email");
                break;
              }
            }
          }
          
          // Last resort: first email
          if (email == null) {
            email = (String) emails.get(0).get("email");
          }
        }
      }
      
      // Step 4: Return success response with user data
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("email", email);
      response.put("githubUsername", userData.get("login"));
      response.put("name", userData.get("name"));
      response.put("avatarUrl", userData.get("avatar_url"));
      
      System.out.println("Mobile OAuth - User email: " + email);
      
      return response;
      
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Failed to exchange code: " + e.getMessage());
      e.printStackTrace();
      return error;
    }
  }
}