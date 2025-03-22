package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;


@Data
@Schema(description = "Match with arena and teams")
public class MatchDtoWithArenaAndTeams {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    @Schema(description = "Arena where plays")
    private ArenaDto arenaDto;
    @Schema(description = "List of teams in match")
    private List<TeamDtoWithPlayers> teamDtoWithPlayersList;
}
