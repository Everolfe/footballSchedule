package com.github.everolfe.footballmatches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;


@Entity
@Table(name = "matches")
@Data
@Schema(description = "Match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "Date and time cannot be null")
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @NotNull(message = "Tournament name cannot be null")
    @Size(min = 1, max = 100, message = "Tournament name must be between 1 and 100 characters")
    @Column(name = "tournament_name")
    private String tournamentName;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JsonIgnoreProperties("matchList")
    @JoinTable(name = "match_teams",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    @Schema(description = "List of teams in match")
    private List<Team> teamList;

    @ManyToOne
    @JoinColumn(name = "arena_id")
    @JsonIgnoreProperties("matchList")
    @Schema(description = "Arena where plays")
    private Arena arena;
}