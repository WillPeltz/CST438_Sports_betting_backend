package com.example.restservice.service;
import com.example.restservice.entity.Game;  // ADD THIS
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
import com.example.restservice.repository.PlayerRepository; 
import com.example.restservice.entity.Player;
import java.util.ArrayList;
import java.util.List;
import com.example.restservice.repository.GameRepository;  // ADD THIS
import java.time.LocalDateTime;  // ADD THIS
import java.time.format.DateTimeFormatter;  // ADD THIS

@Service
public class BallDontLieService {
    
    @Value("${balldontlie.api.key}")
    private String apiKey;
    
    @Value("${balldontlie.api.url}")
    private String apiUrl;
    
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;  // ADD THIS LINE


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<Team> fetchAndSaveTeams() {
        try {
            // setting up headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // gettig teams from BallDontLie
            String url = apiUrl + "/teams";
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            // parsing JSON response
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
            
            // saving teams
            teamRepository.saveAll(teams);
            
            return teams;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

   public List<Player> fetchAndSavePlayers() {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        List<Player> allPlayers = new ArrayList<>();
        Integer cursor = null;
        int pageCount = 0;
        int retryCount = 0;
        int maxRetries = 3;
        
        // keeps fetching data
        while (true) {
            pageCount++;
            System.out.println("Fetching page " + pageCount + " (cursor: " + cursor + ")...");
            
            
            String url = apiUrl + "/players?per_page=100";
            if (cursor != null) {
                url += "&cursor=" + cursor;
            }
            
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    String.class
                );
                
                retryCount = 0;
                
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode playersData = root.get("data");
                
                System.out.println("Found " + playersData.size() + " players on page " + pageCount);
                
                if (playersData.size() == 0) {
                    System.out.println("No more players, stopping...");
                    break;
                }
            
            for (JsonNode playerNode : playersData) {
                Player player = new Player();
                player.setId(playerNode.get("id").asLong());
                player.setFirstName(playerNode.get("first_name").asText());
                player.setLastName(playerNode.get("last_name").asText());
                player.setPosition(playerNode.get("position").asText(""));
                
                // handling nullable fields 
                if (playerNode.has("height") && !playerNode.get("height").isNull() && !playerNode.get("height").asText().isEmpty()) {
                    player.setHeight(parseHeight(playerNode.get("height").asText()));
                }
                if (playerNode.has("weight") && !playerNode.get("weight").isNull() && !playerNode.get("weight").asText().isEmpty()) {
                    try {
                        player.setWeight(Integer.parseInt(playerNode.get("weight").asText()));
                    } catch (NumberFormatException e) {
                        player.setWeight(null);
                    }
                }
                if (playerNode.has("jersey_number") && !playerNode.get("jersey_number").isNull() && !playerNode.get("jersey_number").asText().isEmpty()) {
                    player.setJerseyNumber(playerNode.get("jersey_number").asText());
                }
                if (playerNode.has("college") && !playerNode.get("college").isNull() && !playerNode.get("college").asText().isEmpty()) {
                    player.setCollege(playerNode.get("college").asText());
                }
                if (playerNode.has("country") && !playerNode.get("country").isNull() && !playerNode.get("country").asText().isEmpty()) {
                    player.setCountry(playerNode.get("country").asText());
                }
                if (playerNode.has("draft_year") && !playerNode.get("draft_year").isNull()) {
                    player.setDraftYear(playerNode.get("draft_year").asInt(0));
                }
                if (playerNode.has("draft_round") && !playerNode.get("draft_round").isNull()) {
                    player.setDraftRound(playerNode.get("draft_round").asInt(0));
                }
                if (playerNode.has("draft_number") && !playerNode.get("draft_number").isNull()) {
                    player.setDraftNumber(playerNode.get("draft_number").asInt(0));
                }
                
                // linking to their team
                if (playerNode.has("team") && !playerNode.get("team").isNull()) {
                    Long teamId = playerNode.get("team").get("id").asLong();
                    player.setTeam(teamRepository.findById(teamId).orElse(null));
                }
                
                allPlayers.add(player);
            }
            
            JsonNode meta = root.get("meta");
            if (meta != null && meta.has("next_cursor") && !meta.get("next_cursor").isNull()) {
                cursor = meta.get("next_cursor").asInt();
                System.out.println("Next cursor: " + cursor);
            } else {
                System.out.println("No more pages");
                break;
            }
            
            // had to get help for this as I was confused when going over API calls
            Thread.sleep(12000);
            
            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    System.err.println("Max retries reached. Stopping fetch. Players saved so far: " + allPlayers.size());
                    break;
                }
                int waitTime = retryCount * 30000;
                System.out.println("Rate limit hit. Waiting " + (waitTime/1000) + " seconds before retry " + retryCount + "...");
                Thread.sleep(waitTime);
                pageCount--; 
                continue; 
            }
        }
        
        System.out.println("Total players fetched: " + allPlayers.size());
        playerRepository.saveAll(allPlayers);
        return allPlayers;
        
    } catch (Exception e) {
        System.err.println("Error fetching players: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}

// search players directly from API 
public List<Player> searchPlayersFromAPI(String searchTerm) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        //search url
        String url = apiUrl + "/players?search=" + searchTerm;
        
        ResponseEntity<String> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            String.class
        );
        
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode playersData = root.get("data");
        
        List<Player> players = new ArrayList<>();
        
        for (JsonNode playerNode : playersData) {
            Player player = new Player();
            player.setId(playerNode.get("id").asLong());
            player.setFirstName(playerNode.get("first_name").asText());
            player.setLastName(playerNode.get("last_name").asText());
            player.setPosition(playerNode.get("position").asText(""));
            
            if (playerNode.has("height") && !playerNode.get("height").isNull() && !playerNode.get("height").asText().isEmpty()) {
                player.setHeight(parseHeight(playerNode.get("height").asText()));
            }
            if (playerNode.has("weight") && !playerNode.get("weight").isNull() && !playerNode.get("weight").asText().isEmpty()) {
                try {
                    player.setWeight(Integer.parseInt(playerNode.get("weight").asText()));
                } catch (NumberFormatException e) {
                    player.setWeight(null);
                }
            }
            if (playerNode.has("jersey_number") && !playerNode.get("jersey_number").isNull() && !playerNode.get("jersey_number").asText().isEmpty()) {
                player.setJerseyNumber(playerNode.get("jersey_number").asText());
            }
            if (playerNode.has("college") && !playerNode.get("college").isNull() && !playerNode.get("college").asText().isEmpty()) {
                player.setCollege(playerNode.get("college").asText());
            }
            if (playerNode.has("country") && !playerNode.get("country").isNull() && !playerNode.get("country").asText().isEmpty()) {
                player.setCountry(playerNode.get("country").asText());
            }
            if (playerNode.has("draft_year") && !playerNode.get("draft_year").isNull()) {
                player.setDraftYear(playerNode.get("draft_year").asInt(0));
            }
            if (playerNode.has("draft_round") && !playerNode.get("draft_round").isNull()) {
                player.setDraftRound(playerNode.get("draft_round").asInt(0));
            }
            if (playerNode.has("draft_number") && !playerNode.get("draft_number").isNull()) {
                player.setDraftNumber(playerNode.get("draft_number").asInt(0));
            }
            
            // Link to team if it exists
            if (playerNode.has("team") && !playerNode.get("team").isNull()) {
                Long teamId = playerNode.get("team").get("id").asLong();
                player.setTeam(teamRepository.findById(teamId).orElse(null));
            }
            
            players.add(player);
        }
        
        return players;
        
    } catch (Exception e) {
        System.err.println("Error searching players from API: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}
// Fetch games for a date range (e.g., next 7 days)
public List<Game> fetchAndSaveGames(LocalDateTime startDate, LocalDateTime endDate) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        List<Game> allGames = new ArrayList<>();
        Integer cursor = null;
        int pageCount = 0;
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(dateFormatter);
        String endDateStr = endDate.format(dateFormatter);
        
        while (true) {
            pageCount++;
            System.out.println("Fetching games page " + pageCount + " (cursor: " + cursor + ")...");
            
            // Build URL with date range
            String url = apiUrl + "/games?start_date=" + startDateStr + "&end_date=" + endDateStr + "&per_page=100";
            if (cursor != null) {
                url += "&cursor=" + cursor;
            }
            
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    String.class
                );
                
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode gamesData = root.get("data");
                
                System.out.println("Found " + gamesData.size() + " games on page " + pageCount);
                
                if (gamesData.size() == 0) {
                    System.out.println("No more games, stopping...");
                    break;
                }
                
                for (JsonNode gameNode : gamesData) {
                    Game game = new Game();
                    game.setId(gameNode.get("id").asLong());
                    
                    // Parse date - FIXED VERSION
                    String dateStr = gameNode.get("date").asText();
                    try {
                        if (dateStr.length() >= 19) {
                            game.setDate(LocalDateTime.parse(dateStr.substring(0, 19)));
                        } else if (dateStr.contains("T")) {
                            game.setDate(LocalDateTime.parse(dateStr));
                        } else {
                            game.setDate(LocalDateTime.parse(dateStr + "T00:00:00"));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing date: " + dateStr);
                        game.setDate(LocalDateTime.now());
                    }
                    
                    game.setSeason(gameNode.get("season").asInt());
                    game.setStatus(gameNode.get("status").asText());
                    
                    if (gameNode.has("home_team_score") && !gameNode.get("home_team_score").isNull()) {
                        game.setHomeTeamScore(gameNode.get("home_team_score").asInt());
                    }
                    if (gameNode.has("visitor_team_score") && !gameNode.get("visitor_team_score").isNull()) {
                        game.setVisitorTeamScore(gameNode.get("visitor_team_score").asInt());
                    }
                    if (gameNode.has("period") && !gameNode.get("period").isNull()) {
                        game.setPeriod(gameNode.get("period").asInt());
                    }
                    if (gameNode.has("time") && !gameNode.get("time").isNull()) {
                        game.setTime(gameNode.get("time").asText());
                    }
                    if (gameNode.has("postseason") && !gameNode.get("postseason").isNull()) {
                        game.setPostseason(gameNode.get("postseason").asBoolean());
                    }
                    
                    // Link to teams
                    if (gameNode.has("home_team") && !gameNode.get("home_team").isNull()) {
                        Long homeTeamId = gameNode.get("home_team").get("id").asLong();
                        game.setHomeTeam(teamRepository.findById(homeTeamId).orElse(null));
                    }
                    if (gameNode.has("visitor_team") && !gameNode.get("visitor_team").isNull()) {
                        Long visitorTeamId = gameNode.get("visitor_team").get("id").asLong();
                        game.setVisitorTeam(teamRepository.findById(visitorTeamId).orElse(null));
                    }
                    
                    allGames.add(game);
                }
                
                // Get next cursor
                JsonNode meta = root.get("meta");
                if (meta != null && meta.has("next_cursor") && !meta.get("next_cursor").isNull()) {
                    cursor = meta.get("next_cursor").asInt();
                    System.out.println("Next cursor: " + cursor);
                } else {
                    System.out.println("No more pages");
                    break;
                }
                
                Thread.sleep(12000); // Free tier rate limit
                
            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                System.out.println("Rate limit hit. Waiting 30 seconds...");
                Thread.sleep(30000);
                pageCount--;
                continue;
            }
        }
        
        System.out.println("Total games fetched: " + allGames.size());
        gameRepository.saveAll(allGames);
        return allGames;
        
    } catch (Exception e) {
        System.err.println("Error fetching games: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    }
}

//parse heights 
private Integer parseHeight(String height) {
    try {
        if (height == null || height.isEmpty()) return null;
        String[] parts = height.split("-");
        int feet = Integer.parseInt(parts[0]);
        int inches = Integer.parseInt(parts[1]);
        return (feet * 12) + inches;
    } catch (Exception e) {
        return null;
    }
}
    
}