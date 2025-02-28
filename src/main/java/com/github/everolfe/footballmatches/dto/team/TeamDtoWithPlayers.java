package com.github.everolfe.footballmatches.dto.team;

import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import java.util.List;
import lombok.Data;


@Data
public class TeamDtoWithPlayers {
    private Integer id;
    private String teamName;
    private String country;
    private List<PlayerDto> playerDtoList;
}
