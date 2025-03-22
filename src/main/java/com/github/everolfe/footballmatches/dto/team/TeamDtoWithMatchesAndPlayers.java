package com.github.everolfe.footballmatches.dto.team;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Team with matches and players")
public class TeamDtoWithMatchesAndPlayers {
    private Integer id;
    private String teamName;
    private String country;
    @Schema(description = "List of matches for team")
    private List<MatchDtoWithArena> matchDtoWithArenaList;
    @Schema(description = "List of players in team")
    private List<PlayerDto> playerDtoList;
}
