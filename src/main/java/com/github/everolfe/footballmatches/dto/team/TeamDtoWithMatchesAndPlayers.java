package com.github.everolfe.footballmatches.dto.team;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import java.util.List;
import lombok.Data;

@Data
public class TeamDtoWithMatchesAndPlayers {
    private Integer id;
    private String teamName;
    private String country;
    private List<MatchDtoWithArena> matchDtoWithArenaList;
    private List<PlayerDto> playerDtoList;
}
