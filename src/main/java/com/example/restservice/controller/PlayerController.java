package com.example.restservice.controller;
import com.example.restservice.entity.Player;
import com.example.restservice.repository.PlayerRepository;
import com.example.restservice.service.BallDontLieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {
    
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private BallDontLieService ballDontLieService;
    
    // all players
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
    
    // player by their ID
    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable Long id) {
        return playerRepository.findById(id).orElse(null);
    }
    
    // fetching players from API
    @PostMapping("/fetch")
    public List<Player> fetchPlayersFromAPI() {
        return ballDontLieService.fetchAndSavePlayers();
    }

    // players by team
    @GetMapping("/team/{teamId}")
    public List<Player> getPlayersByTeam(@PathVariable Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }
    
    // players by position
    @GetMapping("/position/{position}")
    public List<Player> getPlayersByPosition(@PathVariable String position) {
        return playerRepository.findByPosition(position);
    }
    
    // players by name search (local)
    @GetMapping("/search/{name}")
    public List<Player> searchPlayers(@PathVariable String name) {
        return playerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }
    
    // players by name search (uses API )
    @GetMapping("/search/api/{name}")
    public List<Player> searchPlayersFromAPI(@PathVariable String name) {
        return ballDontLieService.searchPlayersFromAPI(name);
    }
}