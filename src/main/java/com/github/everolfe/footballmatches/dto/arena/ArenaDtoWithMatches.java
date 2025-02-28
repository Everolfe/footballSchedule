package com.github.everolfe.footballmatches.dto.arena;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithTeams;
import java.util.List;
import lombok.Data;

@Data
public class ArenaDtoWithMatches {
    private Integer id;
    private String city;
    private Integer capacity;
    private List<MatchDtoWithTeams> matchDtoWithTeamsList;

}
