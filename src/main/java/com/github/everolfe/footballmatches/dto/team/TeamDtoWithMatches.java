package com.github.everolfe.footballmatches.dto.team;


import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Team")
public class TeamDtoWithMatches {
    private Integer id;
    private String teamName;
    private String country;
    @Schema(description = "List of matches for team")
    private List<MatchDtoWithArena> matchDtoWithArenaList;
}
