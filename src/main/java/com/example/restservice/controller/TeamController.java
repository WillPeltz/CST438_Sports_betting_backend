package com.example.restservice.controller;

import com.example.restservice.entity.Team;
import com.example.restservice.repository.TeamRepository;
import com.example.restservice.service.BallDontLieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private BallDontLieService ballDontLieService;
    
    // GET all teams
    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    // GET team by ID
    @GetMapping("/{id}")
    public Team getTeamById(@PathVariable Long id) {
        return teamRepository.findById(id).orElse(null);
    }
    
    // POST fetch teams from BallDontLie API
    @PostMapping("/fetch")
    public List<Team> fetchTeamsFromAPI() {
        return ballDontLieService.fetchAndSaveTeams();
    }
    
    // POST create new team
    @PostMapping
    public Team createTeam(@RequestBody Team team) {
        return teamRepository.save(team);
    }
    
    // PUT update team
    @PutMapping("/{id}")
    public Team updateTeam(@PathVariable Long id, @RequestBody Team teamDetails) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team != null) {
            team.setName(teamDetails.getName());
            team.setCity(teamDetails.getCity());
            team.setAbbreviation(teamDetails.getAbbreviation());
            team.setConference(teamDetails.getConference());
            team.setDivision(teamDetails.getDivision());
            team.setFullName(teamDetails.getFullName());
            return teamRepository.save(team);
        }
        return null;
    }
    
    // DELETE team
    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamRepository.deleteById(id);
    }
}