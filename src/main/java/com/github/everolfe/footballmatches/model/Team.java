package com.github.everolfe.footballmatches.model;


import java.util.ArrayList;
import java.util.List;

public class Team {
    private Integer id;
    private String teamName;
    private String country;
    private List<Match> matches;
    private List<Player> players;

    public Team(Integer id, String teamName, String country) {
        this.id = id;
        this.teamName = teamName;
        this.country = country;
        this.matches = new ArrayList<Match>();
        this.players = new ArrayList<Player>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
