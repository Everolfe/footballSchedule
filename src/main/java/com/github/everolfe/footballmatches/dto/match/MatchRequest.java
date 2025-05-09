package com.github.everolfe.footballmatches.dto.match;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MatchRequest {
    private String tournamentName;
    private LocalDateTime dateTime;
    private Integer homeTeamId;
    private Integer awayTeamId;
    private Integer arenaId;

}
