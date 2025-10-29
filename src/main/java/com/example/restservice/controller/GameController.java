package com.example.restservice.controller;

import com.example.restservice.entity.Game;
import com.example.restservice.repository.GameRepository;
import com.example.restservice.service.BallDontLieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private BallDontLieService ballDontLieService;

    // GET all games
    @GetMapping("/getAllGames")
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // GET game by ID
    @GetMapping("/{id}")
    public Game getGameById(@PathVariable Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    // GET games happening today
    @GetMapping("/today")
    public List<Game> getTodaysGames(
        @RequestParam(required = false, defaultValue = "America/Los_Angeles") String timezone
    ) {
        ZoneId zone = ZoneId.of(timezone);
        LocalDate today = LocalDate.now(zone);
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        return gameRepository.findByDateBetween(startOfDay, endOfDay);

    }

    // GET upcoming games (next 7 days)
    @GetMapping("/upcoming")
    public List<Game> getUpcomingGames() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        return gameRepository.findByDateBetween(now, nextWeek);
    }

    // GET past games (last 7 days)
    @GetMapping("/past")
    public List<Game> getPastGames() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusDays(7);
        return gameRepository.findByDateBetween(lastWeek, now);
    }

    // GET live games (in progress)
    @GetMapping("/live")
    public List<Game> getLiveGames() {
        return gameRepository.findByStatus("in_progress");
    }

    // GET games for a specific team
    @GetMapping("/team/{teamId}")
    public List<Game> getGamesByTeam(@PathVariable Long teamId) {
        return gameRepository.findByTeamId(teamId);
    }

    // GET games by date range
    @GetMapping("/range")
    public List<Game> getGamesByDateRange(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
        return gameRepository.findByDateBetween(start, end);
    }

    // GET games by season
    @GetMapping("/season/{season}")
    public List<Game> getGamesBySeason(@PathVariable Integer season) {
        return gameRepository.findBySeason(season);
    }

    // POST fetch games from API for next 7 days
    @PostMapping("/fetch")
    public List<Game> fetchGamesFromAPI() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        return ballDontLieService.fetchAndSaveGames(now, nextWeek);
    }

    // POST fetch games for custom date range
    @PostMapping("/fetch/range")
    public List<Game> fetchGamesForDateRange(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
        return ballDontLieService.fetchAndSaveGames(start, end);
    }
}