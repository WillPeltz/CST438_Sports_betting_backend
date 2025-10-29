package com.example.restservice.controller;

import com.example.restservice.entity.Team;
import com.example.restservice.repository.TeamRepository;
import com.example.restservice.service.BallDontLieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private BallDontLieService ballDontLieService;

    // Get all teams
    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    // Get team by id
    @GetMapping("/{id}")
    public Team getTeamById(@PathVariable Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    // Fetch teams from API
    @PostMapping("/fetch")
    public List<Team> fetchTeamsFromAPI() {
        return ballDontLieService.fetchAndSaveTeams();
    }

    // Create new team
    @PostMapping
    public Team createTeam(@RequestBody Team team) {
        return teamRepository.save(team);
    }

    // Update team
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @RequestBody Team teamDetails) {
        return teamRepository.findById(id)
            .map(team -> {
                team.setName(teamDetails.getName());
                team.setCity(teamDetails.getCity());
                team.setAbbreviation(teamDetails.getAbbreviation());
                team.setConference(teamDetails.getConference());
                team.setDivision(teamDetails.getDivision());
                team.setFullName(teamDetails.getFullName());
                team.setUpdatedAt(LocalDateTime.now());
                
                Team updatedTeam = teamRepository.save(team);
                return ResponseEntity.ok(updatedTeam);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Delete team
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
            .map(team -> {
                teamRepository.delete(team);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}