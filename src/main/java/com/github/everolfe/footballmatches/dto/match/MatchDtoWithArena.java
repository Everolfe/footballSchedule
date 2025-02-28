package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import java.time.LocalDateTime;
import lombok.Data;


@Data
public class MatchDtoWithArena {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    private ArenaDto arenaDto;
}
