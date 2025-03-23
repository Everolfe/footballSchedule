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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Team name cannot be null")
    @Size(min = 1, max = 100, message = "Team name must be between 1 and 100 characters")
    @Column(name = "team_name")
    private String teamName;

    @NotNull(message = "Country  cannot be null")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
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
