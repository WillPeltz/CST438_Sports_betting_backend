package com.example.restservice.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // fix auto gen ID to be able to create teams
    
    @Column(nullable = false)
    private String abbreviation;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String conference;
    
    @Column(nullable = false)
    private String division;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // constructors
    public Team() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //gets and sets
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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