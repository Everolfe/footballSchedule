package com.github.everolfe.footballmatches.dto.arena;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithTeams;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Arena with matchList")
public class ArenaDtoWithMatches {
    private Integer id;
    private String city;
    private Integer capacity;
    @Schema(description = "Match list")
    private List<MatchDtoWithTeams> matchDtoWithTeamsList;

}
