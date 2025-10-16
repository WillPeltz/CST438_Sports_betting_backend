package com.example.restservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
public class Player {
    
    @Id
    private Long id; 
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(length = 50)
    private String position;
    
    private Integer height; // in inches
    
    private Integer weight; // in lbs
    
    @Column(name = "jersey_number")
    private String jerseyNumber;
    
    private String college;
    
    private String country;
    
    @Column(name = "draft_year")
    private Integer draftYear;
    
    @Column(name = "draft_round")
    private Integer draftRound;
    
    @Column(name = "draft_number")
    private Integer draftNumber;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Player() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getJerseyNumber() {
        return jerseyNumber;
    }
    
    public void setJerseyNumber(String jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }
    
    public String getCollege() {
        return college;
    }
    
    public void setCollege(String college) {
        this.college = college;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Integer getDraftYear() {
        return draftYear;
    }
    
    public void setDraftYear(Integer draftYear) {
        this.draftYear = draftYear;
    }
    
    public Integer getDraftRound() {
        return draftRound;
    }
    
    public void setDraftRound(Integer draftRound) {
        this.draftRound = draftRound;
    }
    
    public Integer getDraftNumber() {
        return draftNumber;
    }
    
    public void setDraftNumber(Integer draftNumber) {
        this.draftNumber = draftNumber;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}