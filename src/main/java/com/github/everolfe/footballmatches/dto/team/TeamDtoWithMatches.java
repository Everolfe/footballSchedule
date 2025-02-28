package com.github.everolfe.footballmatches.dto.team;


import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import java.util.List;
import lombok.Data;

@Data
public class TeamDtoWithMatches {
    private Integer id;
    private String teamName;
    private String country;
    private List<MatchDtoWithArena> matchDtoWithArenaList;
}
