package com.github.everolfe.footballmatches.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
public class Match {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    private List<Team> teamList = new ArrayList<Team>();
    private Arena arena;

    public Match(Integer id, LocalDateTime dateTime, String tournamentName) {
        this.id = id;
        this.dateTime = dateTime;
        this.tournamentName = tournamentName;
    }
}