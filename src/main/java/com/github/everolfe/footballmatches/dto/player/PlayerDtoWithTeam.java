package com.github.everolfe.footballmatches.dto.player;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatches;
import lombok.Data;

@Data
public class PlayerDtoWithTeam {
    private Integer id;
    private String name;
    private Integer age;
    private String country;
    private TeamDtoWithMatches teamDtoWithMatches;
}
