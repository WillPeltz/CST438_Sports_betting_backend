package com.example.restservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game {
    
    @Id
    private Long id;
    
    @Column(name = "date")
    private LocalDateTime date;
    
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    
    @ManyToOne
    @JoinColumn(name = "visitor_team_id")
    private Team visitorTeam;
    
    @Column(name = "season")
    private Integer season;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "home_team_score")
    private Integer homeTeamScore;
    
    @Column(name = "visitor_team_score")
    private Integer visitorTeamScore;
    
    @Column(name = "period")
    private Integer period;
    
    @Column(name = "time")
    private String time;
    
    @Column(name = "postseason")
    private Boolean postseason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public Team getHomeTeam() {
        return homeTeam;
    }
    
    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }
    
    public Team getVisitorTeam() {
        return visitorTeam;
    }
    
    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
    }
    
    public Integer getSeason() {
        return season;
    }
    
    public void setSeason(Integer season) {
        this.season = season;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getHomeTeamScore() {
        return homeTeamScore;
    }
    
    public void setHomeTeamScore(Integer homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }
    
    public Integer getVisitorTeamScore() {
        return visitorTeamScore;
    }
    
    public void setVisitorTeamScore(Integer visitorTeamScore) {
        this.visitorTeamScore = visitorTeamScore;
    }
    
    public Integer getPeriod() {
        return period;
    }
    
    public void setPeriod(Integer period) {
        this.period = period;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public Boolean getPostseason() {
        return postseason;
    }
    
    public void setPostseason(Boolean postseason) {
        this.postseason = postseason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}