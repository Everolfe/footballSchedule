package com.github.everolfe.footballmatches.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Match {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    private List<Team> teamList;
    private Arena arena;

    public Match(Integer id, LocalDateTime dateTime, String tournamentName){
        this.id = id;
        this.dateTime = dateTime;
        this.tournamentName = tournamentName;
        this.teamList = new ArrayList<Team>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public List<Team> getTeamList() {
        return teamList;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }
}