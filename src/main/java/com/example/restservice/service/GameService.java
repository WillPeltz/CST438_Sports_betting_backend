package com.example.restservice.service;
import org.springframework.stereotype.Service;
import com.example.restservice.entity.Game;
import com.example.restservice.repository.GameRepository;
import java.util.List;
@Service
public class GameService {
    private final GameRepository gameRepository;
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}
