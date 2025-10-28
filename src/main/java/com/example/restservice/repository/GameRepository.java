package com.example.restservice.repository;

import com.example.restservice.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    
    //  games by date 
    List<Game> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    //  games for specific team
    @Query("SELECT g FROM Game g WHERE g.homeTeam.id = :teamId OR g.visitorTeam.id = :teamId")
    List<Game> findByTeamId(@Param("teamId") Long teamId);
    
    // upcoming games
    List<Game> findByStatusAndDateAfter(String status, LocalDateTime date);
    
    // games by season
    List<Game> findBySeason(Integer season);
    
    // games by status
    List<Game> findByStatus(String status);
}