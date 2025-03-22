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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "teams")
@Data
@Schema(description = "Team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "country")
    private String country;

    @JsonIgnoreProperties("teamList")
    @ManyToMany(mappedBy = "teamList", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
                       CascadeType.MERGE, CascadeType.DETACH})
    @Schema(description = "List of matches for team")
    private List<Match> matches;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
                       CascadeType.DETACH})
    @Schema(description = "List of players in team")
    private List<Player> players;
}
