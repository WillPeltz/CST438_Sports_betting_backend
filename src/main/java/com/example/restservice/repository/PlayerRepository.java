package com.example.restservice.repository;

import com.example.restservice.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Find players by team
    List<Player> findByTeamId(Long teamId);
    
    // Find players by position
    List<Player> findByPosition(String position);
    
    // Find players by name (case-insensitive)
    List<Player> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName
    );
}
