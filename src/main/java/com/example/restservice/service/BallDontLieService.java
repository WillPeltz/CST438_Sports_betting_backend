package com.example.restservice.service;

import com.example.restservice.entity.Team;
import com.example.restservice.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class BallDontLieService {
    
    @Value("${balldontlie.api.key}")
    private String apiKey;
    
    @Value("${balldontlie.api.url}")
    private String apiUrl;
    
    @Autowired
    private TeamRepository teamRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<Team> fetchAndSaveTeams() {
        try {
            // Set up headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Fetch teams from BallDontLie
            String url = apiUrl + "/teams";
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode teamsData = root.get("data");
            
            List<Team> teams = new ArrayList<>();
            
            for (JsonNode teamNode : teamsData) {
                Team team = new Team();
                team.setId(teamNode.get("id").asLong());
                team.setAbbreviation(teamNode.get("abbreviation").asText());
                team.setCity(teamNode.get("city").asText());
                team.setConference(teamNode.get("conference").asText());
                team.setDivision(teamNode.get("division").asText());
                team.setFullName(teamNode.get("full_name").asText());
                team.setName(teamNode.get("name").asText());
                
                teams.add(team);
            }
            
            // Save all teams to database
            teamRepository.saveAll(teams);
            
            return teams;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}