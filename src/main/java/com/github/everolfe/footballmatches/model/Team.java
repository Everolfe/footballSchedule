package com.github.everolfe.footballmatches.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Team {
    private Integer id;
    private String teamName;
    private String country;
    private List<Match> matches = new ArrayList<Match>();
    private List<Player> players = new ArrayList<Player>();

    public Team(Integer id, String teamName, String country) {
        this.id = id;
        this.teamName = teamName;
        this.country = country;
    }

}
