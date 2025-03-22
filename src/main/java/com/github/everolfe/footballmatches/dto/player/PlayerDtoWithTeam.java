package com.github.everolfe.footballmatches.dto.player;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Player with team")
public class PlayerDtoWithTeam {
    private Integer id;
    private String name;
    private Integer age;
    private String country;
    @Schema(description = "Team where player in")
    private TeamDtoWithMatches teamDtoWithMatches;
}
