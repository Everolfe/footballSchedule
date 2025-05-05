package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.model.Match;
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
