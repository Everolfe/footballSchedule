package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;


@Data
public class MatchDtoWithTeams {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    private List<TeamDtoWithPlayers> teamDtoWithPlayersList;
}
